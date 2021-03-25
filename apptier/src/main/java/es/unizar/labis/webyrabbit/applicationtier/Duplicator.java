package es.unizar.labis.webyrabbit.applicationtier;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class Duplicator {
	// Esta clase hace trabajo de "dominio" (o aplicación/dominio)
	// de nuestro ejemplo. Sabe multiplicar un número por 2.


	// Este método es un RabbitListener sobre la cola duplicar. La
	// anotación hace el binding de esa cola con la centralita por
	// defecto con clave de binding duplicar.
	// RabbitListener se encarga de poner el resultado en la cola
	// reply-to del mensaje sin que lo tengamos que mirar nosotros.
	@RabbitListener(queues="duplicar")
	public Integer receive(Integer value) {
		return value*2;
	}
}