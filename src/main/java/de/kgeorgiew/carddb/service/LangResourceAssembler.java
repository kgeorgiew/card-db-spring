package de.kgeorgiew.carddb.service;

import de.kgeorgiew.carddb.domain.Lang;
import de.kgeorgiew.carddb.web.LangController;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Service;

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

        Link selfRel = linkTo(methodOn(controller).get(entity.getLang())).withSelfRel();
        // TODO Fix link
        Link deleteRel = linkTo(methodOn(controller).get(entity.getLang())).withRel("delete");

        // TODO Can this link be created with methodOn(..).update(ID, Entity) ??
        Link updateRel = linkTo(methodOn(controller).get(entity.getLang())).withRel("update");

        return new Resource<>(entity, selfRel, deleteRel, updateRel);
    }
}
