package es.unizar.labis.webyrabbit.webtier;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class DivideController {
	private ConcurrentHashMap<String, Integer> dict = new ConcurrentHashMap<String, Integer>();
	private final RabbitTemplate rabbitTemplate;

	public DivideController(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	// Por defecto, este método responderá a peticiones de tipo
	// http://localhost:8080/divide?value=420
	// con la respuesta "Tu resultado es 210!"
	@GetMapping("/divide")
	public String hello(@RequestParam(value = "value") int value) {
		String correlationId = UUID.randomUUID().toString();
		// Enviamos el mensaje con payload value, a la cola dividir (centralita
		// por defecto, clave de enrutado dividir), indicando que la respuesta
		// debe ser sobre la cola response y poniendo un correlationId
		// para luego recoger el resultado de nuestra operación y no otro
		// que ha podido pedir otro cliente.
		rabbitTemplate.convertAndSend("dividir", value,
				m -> {
					m.getMessageProperties().setReplyTo("response");
					m.getMessageProperties().setCorrelationId(correlationId);
					return m;
				});
		return String.format("Tu resultado es %d!", getResponseFor(correlationId));
	}

	// Este método escucha sobre la cola response y cuando llega algo
	// saca su correlation id y lo guarda en un diccionario con ese
	// id como clave
	@RabbitListener(queues="response")
	public void receive(@Header(AmqpHeaders.CORRELATION_ID) String correlationId,
						@Payload Integer value) {
		dict.put(correlationId, value);
	}

	// Este método es síncrono. Le pides el valor asociado a un cierto
	// correlationId y mientras ese valor no esté en dict se dedicará
	// a dormir.
	public Integer getResponseFor(String correlationId) {
		Integer response = dict.get(correlationId);
		while (response == null) {
			try {
				// Dormimos este hilo 50 ms. El hilo no se podrá
				// reusar, pero no estamos consumiendo CPU.
				Thread.sleep(50);
			} catch (Exception e) {
				// No hacemos nada si nos interrumpen, no nos importa
			}
			response = dict.get(correlationId);
		}
		// Ya lo podemos borrar, este no lo volveremos a necesitar
		dict.remove(correlationId);
		return response;
	}

	// Este ejemplo no gestiona automáticamente cualquier escenario normal.
	// P.ej. si lanzamos dos instancias de webtier en dos puertos distintos
	// y les pedimos a los dos que hagan divisiones, ambos competirán por las
	// respuestas en la cola "response" y acabarán llegando las respuestas
	// al sitio equivocado, con lo el otro webtier se quedará esperando
	// eternamente.
	// Una solución a esto sería tener una cola response por cada instancia
	// del componente webtier (response1, response2...) etc.

}
