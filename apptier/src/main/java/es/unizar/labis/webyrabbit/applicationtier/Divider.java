package es.unizar.labis.webyrabbit.applicationtier;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class Divider {
	// Esta clase hace trabajo de "dominio" (o aplicación/dominio)
	// de nuestro ejemplo. Sabe dividir un número por 2.

	// Este método es un RabbitListener sobre la cola dividir. La
	// anotación hace el binding de esa cola con la centralita por
	// defecto con clave de binding dividr.
	// RabbitListener se encarga de poner el resultado en la cola
	// reply-to del mensaje sin que lo tengamos que mirar nosotros.
	@RabbitListener(queues="dividir")
	public Integer receive(Integer value) {
		return value/2;
	}
}
