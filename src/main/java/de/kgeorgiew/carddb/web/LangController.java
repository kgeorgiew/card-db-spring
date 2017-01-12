package de.kgeorgiew.carddb.web;

import de.kgeorgiew.carddb.service.LangRepository;
import de.kgeorgiew.carddb.domain.Lang;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author kgeorgiew
 */
@RestController
@RequestMapping("/api/v1/")
public class LangController {

    private LangRepository repository;

    public LangController(LangRepository repository) {
        this.repository = repository;
    }

    @RequestMapping(value="lang", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.PUT)
    public ResponseEntity<Lang> create(@Valid @RequestBody Lang entity) {
        Lang result = repository.create(entity);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

}
