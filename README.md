# Quick-and-dirty Spring Boot+AMQP example
Clone.
Create `/apptier/src/main/resources/application.properties` and `/webtier/src/main/resources/application.properties` with your RabbitMQ instance configuration. Something like:

```
spring.rabbitmq.host=spotted-monkey.rmq.cloudamqp.com   # the host you have; this is my instance in CloudAMQP
spring.rabbitmq.virtual-host=YOURVHOSTNAME
spring.rabbitmq.port=5672
spring.rabbitmq.username=YOURUSERNAME   # in CloudAMQP is normally the same as the virtual host
spring.rabbitmq.password=YOURPASSWORD
```

Run `./gradlew bootRun` inside the apptier directory, and then in the webtier directory (the order does not matter). JDK/JRE 11 has to be the default Java in your system for Gradle to use it.

Now you can try to make requests like <http://localhost:8080/duplica?value=500> and <http://localhost:8080/divide?value=1000>.
