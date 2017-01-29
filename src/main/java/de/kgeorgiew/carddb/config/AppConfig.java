package de.kgeorgiew.carddb.config;

import de.kgeorgiew.carddb.domain.Lang;
import de.kgeorgiew.carddb.web.LangController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;

import java.time.Clock;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

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
    public ResourceProcessor<Resource<Lang>> personProcessor() {

        return new ResourceProcessor<Resource<Lang>>() {

            @Override
            public Resource<Lang> process(Resource<Lang> resource) {

                resource.add(new Link("http://localhost:8080/people", "added-link"));

//                Link selfRel = linkTo(LangController.class).slash(entity.getLang()).withSelfRel();
//                Link deleteRel = linkTo(LangController.class).slash(entity.getLang()).withRel("delete");
//                Link updateRel = linkTo(LangController.class).slash(entity.getLang()).withRel("update");
                return resource;
            }
        };
    }
}
