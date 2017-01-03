package de.kgeorgiew;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Main controller of the application
 *
 * @author kgeorgiew
 *
 */
@EnableWebMvc
@EnableAutoConfiguration
@SpringBootApplication
public class CardDbSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(CardDbSpringApplication.class, args);
	}
}
