package de.kgeorgiew.carddb.web;

import de.kgeorgiew.carddb.domain.Lang;
import de.kgeorgiew.carddb.exception.ResourceNotFoundException;
import de.kgeorgiew.carddb.service.LangRepository;
import de.kgeorgiew.carddb.service.LangResourceAssembler;
import de.kgeorgiew.carddb.service.MessagesService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

/**
 *
 * Lang resource api endpoint
 *
 * @author kgeorgiew
 */
@RestController
@RequestMapping(value = "/api/v1/lang", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class LangController {

    private final @NonNull LangRepository repository;
    private final @NonNull LangResourceAssembler resourceAssembler;
    private final @NonNull MessagesService messages;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Resource<Lang>> create(@Valid @RequestBody Lang entity) {
        Lang result = repository.create(entity);
        return new ResponseEntity<>(resourceAssembler.toResource(result), HttpStatus.CREATED);
    }

    @RequestMapping(value = "{lang}", method = RequestMethod.GET)
    public ResponseEntity<Resource<Lang>> get(@PathVariable String lang) {
        Optional<Lang> result = repository.get(lang);
        return result.map( entity ->
                new ResponseEntity<>(resourceAssembler.toResource(entity), HttpStatus.OK)
            ).orElseThrow(() ->
                new ResourceNotFoundException(messages.getMessage("error.entity.notFound", lang))
            );
    }

}
