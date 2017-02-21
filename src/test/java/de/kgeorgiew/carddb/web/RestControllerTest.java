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

import static de.kgeorgiew.carddb.HalRestAsserts.assertJsonError;
import static de.kgeorgiew.carddb.RestRequestUtil.jsonRequest;
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
    public void postWithEmptyStringShouldFail() throws Exception {
        ResultActions actualResponse = mvc.perform(jsonRequest(post(TestController.baseUrl), ""));

        HttpStatus expectedStatus = HttpStatus.BAD_REQUEST;

        assertJsonError(actualResponse, expectedStatus);
    }

    @Test
    public void postWithWrongContentTypeShouldFail() throws Exception {
        ResultActions actualResponse = mvc.perform(jsonRequest(post(TestController.baseUrl))
                .contentType(MediaType.TEXT_PLAIN_VALUE));

        HttpStatus expectedStatus = HttpStatus.UNSUPPORTED_MEDIA_TYPE;

        assertJsonError(actualResponse, expectedStatus);
    }

    @Test
    public void getWithWrongPathVariableTypeShouldFail() throws Exception {
        ResultActions actualResponse = mvc.perform(jsonRequest(get(TestController.idUrl, "wrongIdType"))
                .contentType(MediaType.TEXT_PLAIN_VALUE));

        HttpStatus expectedStatus = HttpStatus.BAD_REQUEST;

        assertJsonError(actualResponse, expectedStatus);
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
