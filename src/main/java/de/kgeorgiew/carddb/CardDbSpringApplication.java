package de.kgeorgiew.carddb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

/**
 * Main controller of the application
 *
 * @author kgeorgiew
 *
 */

@SpringBootApplication
@EnableSpringDataWebSupport
@ComponentScan(basePackages = "de.kgeorgiew.carddb")
public class CardDbSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(CardDbSpringApplication.class, args);
	}

}
