package de.kgeorgiew.carddb.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/test")
class TestController {

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<TestPojo> create(@RequestBody TestPojo entity) {
        return new ResponseEntity<>(entity, HttpStatus.CREATED);
    }

    final static class TestPojo {

        private String field;

        TestPojo() {
        }

        public TestPojo(String field) {
            this.field = field;
        }

        public String getField() {
            return field;
        }
    }
}

