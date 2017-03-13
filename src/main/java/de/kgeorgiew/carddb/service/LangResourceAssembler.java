package de.kgeorgiew.carddb.service;

import de.kgeorgiew.carddb.domain.Lang;
import de.kgeorgiew.carddb.web.LangController;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.core.Relation;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * @author kgeorgiew
 */
@Service
public class LangResourceAssembler implements ResourceAssembler<Lang, Resource<Lang>> {

    private final Class<LangController> controller = LangController.class;

    @Override
    public Resource<Lang> toResource(Lang entity) {

        Resource<Lang> resource = new Resource<>(entity);

        resource.add(linkTo(methodOn(controller).get(entity.getLang())).withSelfRel());
        resource.add(linkTo(methodOn(controller).delete(entity.getLang())).withRel("delete"));
        resource.add(linkTo(methodOn(controller).update(entity.getLang(), entity)).withRel("update"));

        return resource;
    }

    /**
     * Converts all given entities into resources.
     *
     * @see #toResource(Object)
     * @param entities must not be {@literal null}.
     * @return list of entities as resource
     */
    public Resources<Resource<Lang>> toResources(Iterable<Lang> entities) {
        Assert.notNull(entities, "Entities should not be null");

        List<Resource<Lang>> result = new ArrayList<>();
        entities.forEach( e -> result.add(toResource(e)));

        return new Resources<>(result);
    }
}
