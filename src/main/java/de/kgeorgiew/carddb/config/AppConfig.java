package de.kgeorgiew.carddb.config;

import com.fasterxml.jackson.databind.Module;
import de.kgeorgiew.carddb.service.time.SystemTimeService;
import de.kgeorgiew.carddb.service.time.TimeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.hateoas.RelProvider;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.hal.CurieProvider;
import org.springframework.hateoas.hal.DefaultCurieProvider;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.validation.ConstraintViolationProblemModule;

/**
 * @author kgeorgiew
 */
@Configuration
public class AppConfig {

    @Bean
    public TimeService timeServiceProvider() {
        return new SystemTimeService();
    }

    @Bean
    public CurieProvider curieProvider() {
        return new DefaultCurieProvider("ex", new UriTemplate("http://www.example.com/rels/{rel}"));
    }

    @Bean
    public RelProvider relProvider() {
        return new AppRelProvider("items");
    }

    @Bean
    public Module problemModule() {
        return new ProblemModule().withStackTraces();
    }

    @Bean
    public Module constraintViolationProblemModule() {
        return new ConstraintViolationProblemModule();
    }

    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasenames("i18n/messages");
        return source;
    }
}
