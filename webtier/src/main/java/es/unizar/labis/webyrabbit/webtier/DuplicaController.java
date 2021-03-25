package es.unizar.labis.webyrabbit.webtier;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class DuplicaController {
	private final RabbitTemplate rabbitTemplate;

	public DuplicaController(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
		// P.ej., si tenemos que cambiar el timeout por defecto
		//rabbitTemplate.setReplyTimeout(9000);

		// Si false, que es el valor por defecto, se usa el direct-reply-to de
		// RabbitMQ <https://www.rabbitmq.com/direct-reply-to.html>
		// Se se pone a true, se usa un cola temporal, exclusiva y que se borra
		// automáticamente. Probablemente no hay una buena razón para preferir
		// esto último si el RabbitMQ es lo bastante reciente para soportar
		// el direct-reply-to.
		//this.rabbitTemplate.setUseTemporaryReplyQueues(true);
	}

	// Este método responderá a peticiones de tipo
	// http://localhost:8080/duplica?value=421
	// con la respuesta "Tu resultado es 842!"
	@GetMapping("/duplica")
	public String hello(@RequestParam(value = "value") int value) {
		// Al usar el convertSendAndReceive dejamos que RabbitTemplate implemente el
		// Request-Reply por nosotros. Envia un mensaje con value (compone automáticamente
		// un objeto Message con ese int), lo envía a la centralita "" con clave
		// de enrutado duplicar, y para la respuesta usa el direct-reply-to de
		// RabbitMQ o crea una cola temporal (podemos configurarlo). Escucha en esa
		// cola hasta que llega una respuesta, saca el payload y lo devuelve, en este
		// caso como Integer

		// Esto gestiona cualquier escenario normal automáticamente, incluyendo si tenemos
		// varios clientes concurrentes haciendo peticiones, puesto que RabbitTemplate
		// crea un consumidor (o reusa uno) para la respuesta de cada petición y
		// así se asegura de que las respuestas llegan a quien hizo las peticiones.

		Integer response = (Integer)this.rabbitTemplate.convertSendAndReceive(
				"duplicar", value);

		// Lo que conseguimos es que efectivamente este método que responde a una
		// petición GET enviando un mensaje y esperando su respuesta parezca un
		// método síncrono, normal(con la diferencia de que hay un timeout y saltará una
		// excepción si la respuesta no llega en X segundos).
		return String.format("Tu resultado es %d!", response);
	}
}
