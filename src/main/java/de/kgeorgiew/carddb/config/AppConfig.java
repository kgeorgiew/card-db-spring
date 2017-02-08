package de.kgeorgiew.carddb.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.hal.CurieProvider;
import org.springframework.hateoas.hal.DefaultCurieProvider;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.validation.ConstraintViolationProblemModule;

import java.time.Clock;

/**
 * @author kgeorgiew
 */
@Configuration
public class AppConfig {

    @Bean("clock")
    public Clock getClock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public CurieProvider curieProvider() {
        return new DefaultCurieProvider("ex", new UriTemplate("http://www.example.com/rels/{rel}"));
    }

    @Bean
    public Module problemModule() {
        return new ProblemModule().withStackTraces();
    }

    @Bean
    public Module constraintViolationProblemModule() {
        return new ConstraintViolationProblemModule();
    }
}
