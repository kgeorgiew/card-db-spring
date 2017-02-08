package de.kgeorgiew.carddb.service;

import de.kgeorgiew.carddb.domain.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Service;

/**
 * @author kgeorgiew
 */
@Service
public class LangResourceAssembler implements ResourceAssembler<Lang, Resource<Lang>> {

    @Autowired
    EntityLinks entityLinks;

    @Override
    public Resource<Lang> toResource(Lang entity) {

        Link selfRel = entityLinks.linkToSingleResource(Lang.class, entity.getLang()).withSelfRel();
        Link deleteRel = entityLinks.linkToSingleResource(Lang.class, entity.getLang()).withRel("delete");
        Link updateRel = entityLinks.linkToSingleResource(Lang.class, entity.getLang()).withRel("update");

        return new Resource<>(entity, selfRel, deleteRel, updateRel);
    }
}
