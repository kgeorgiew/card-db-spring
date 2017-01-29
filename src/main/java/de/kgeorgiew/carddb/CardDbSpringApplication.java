package de.kgeorgiew.carddb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.hateoas.config.EnableEntityLinks;
import org.springframework.hateoas.config.EnableHypermediaSupport;

/**
 * Main controller of the application
 *
 * @author kgeorgiew
 *
 */

@SpringBootApplication
@ComponentScan(basePackages = "de.kgeorgiew.carddb")
@EnableEntityLinks
public class CardDbSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(CardDbSpringApplication.class, args);
	}

}
