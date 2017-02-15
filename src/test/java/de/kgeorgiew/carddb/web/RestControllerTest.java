package de.kgeorgiew.carddb.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.bind.annotation.*;

import static de.kgeorgiew.carddb.HalRestAsserts.assertError;
import static de.kgeorgiew.carddb.HalRestAsserts.jsonRequest;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;

/**
 * Test default controller error handling
 *
 * @author kgeorgiew
 */
@RunWith(SpringRunner.class)
@WebMvcTest(RestControllerTest.TestController.class)
public class RestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void postWithInvalidJsonShouldFail() throws Exception {
        String inputContent = "";

        ResultActions result = mvc.perform(jsonRequest(post(TestController.baseUrl), inputContent));

        assertError(result, HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postWithWrongContentTypeShouldFail() throws Exception {
        ResultActions result = mvc.perform(jsonRequest(post(TestController.baseUrl))
                .contentType(MediaType.TEXT_PLAIN_VALUE));

        assertError(result, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @Test
    public void getWithWrongPathVariableTypeShouldFail() throws Exception {
        String inputId = "wrongIdType";
        ResultActions result = mvc.perform(jsonRequest(get(TestController.idUrl, inputId))
                .contentType(MediaType.TEXT_PLAIN_VALUE));

        String expectedMessage = "Failed to convert value of type 'java.lang.String' to " +
                "required type 'java.lang.Integer'; nested exception is java.lang.NumberFormatException: For input string: \"wrongIdType\"";
        assertError(result, HttpStatus.BAD_REQUEST, expectedMessage);
    }


    @RestController
    final static class TestController {

        static final String baseUrl = "/test";
        static final String idUrl = baseUrl + "/{id}";

        @RequestMapping(value = baseUrl, method = RequestMethod.POST)
        public ResponseEntity<TestPojo> create(@RequestBody TestPojo entity) {
            return new ResponseEntity<>(entity, HttpStatus.CREATED);
        }

        @RequestMapping(value = idUrl, method = RequestMethod.GET)
        public ResponseEntity<TestPojo> get(@PathVariable Integer id) {
            return new ResponseEntity<>(new TestPojo(id, "TestField"), HttpStatus.OK);
        }


        final class TestPojo {

            private Integer id;
            private String field;

            TestPojo() {
            }

            public TestPojo(Integer id, String field) {
                this.id = id;
                this.field = field;
            }

            public Integer getId() {
                return id;
            }

            public String getField() {
                return field;
            }
        }
    }
}
