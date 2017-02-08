package de.kgeorgiew.carddb.web;

import de.kgeorgiew.carddb.domain.Lang;
import de.kgeorgiew.carddb.service.LangRepository;
import de.kgeorgiew.carddb.service.LangResourceAssembler;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author kgeorgiew
 */
@RestController
@RequestMapping(value = "/api/v1/lang", produces = MediaTypes.HAL_JSON_VALUE)
@ExposesResourceFor(Lang.class)
public class LangController {

    private LangRepository repository;
    private LangResourceAssembler resourceAssembler;

    public LangController(LangRepository repository, LangResourceAssembler resourceAssembler) {
        this.repository = repository;
        this.resourceAssembler = resourceAssembler;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Resource<Lang>> create(@Valid @RequestBody Lang entity) {
        Lang result = repository.create(entity);
        return new ResponseEntity<>(resourceAssembler.toResource(result), HttpStatus.CREATED);
    }

//
//    @RequestMapping(value = "{id}", method = RequestMethod.GET)
//    @ResponseStatus(HttpStatus.OK)
//    public Resource<Lang> get(String id) {
//        return null;
//    }

}
