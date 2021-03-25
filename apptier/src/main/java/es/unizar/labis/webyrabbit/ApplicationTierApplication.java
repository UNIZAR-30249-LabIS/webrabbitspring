package es.unizar.labis.webyrabbit;

import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
class ApplicationTierApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApplicationTierApplication.class, args);
	}

	@Bean
	public Queue dividirQueue() {
		return new Queue("dividir");
	}

	@Bean
	public Queue duplicarQueue() {
		return new Queue("duplicar");
	}
}

