package de.kgeorgiew.carddb.config;

import de.kgeorgiew.carddb.web.LangController;
import org.springframework.data.rest.webmvc.RepositoryLinksResource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.stereotype.Component;

@Component
public class LangResourceProcessor implements ResourceProcessor<RepositoryLinksResource> {

    @Override
    public RepositoryLinksResource process(RepositoryLinksResource resource) {
        resource.add(ControllerLinkBuilder.linkTo(LangController.class).withRel("lang"));
        return resource;
    }
}