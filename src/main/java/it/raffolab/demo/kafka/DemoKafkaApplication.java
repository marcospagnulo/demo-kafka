package it.raffolab.demo.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 
 * Run the spring boot application
 * 
 * @author Marco Spagnulo
 *
 */
@SpringBootApplication
@EnableScheduling
public class DemoKafkaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoKafkaApplication.class, args);
	}

}
