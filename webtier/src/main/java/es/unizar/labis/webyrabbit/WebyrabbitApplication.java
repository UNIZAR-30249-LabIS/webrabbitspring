package es.unizar.labis.webyrabbit;

import org.springframework.amqp.core.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WebyrabbitApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebyrabbitApplication.class, args);
	}

	@Bean
	public Queue dividirQueue() {
		return new Queue("dividir");
	}

	@Bean
	public Queue duplicarQueue() {
		return new Queue("duplicar");
	}

	@Bean
	public Queue responseQueue() {
		return new Queue("response");
	}

}
