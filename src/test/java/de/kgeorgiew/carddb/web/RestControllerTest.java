package de.kgeorgiew.carddb.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.bind.annotation.*;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test default controller error handling
 *
 * @author kgeorgiew
 */
@RunWith(SpringRunner.class)
@WebMvcTest(RestControllerTest.TestController.class)
public class RestControllerTest implements CrudAssertTrait {

    @Autowired
    private MockMvc mockMvc;

    @Override
    public ResultActions doRequest(RequestBuilder request) throws Exception {
        return mockMvc.perform(request);
    }

    @Override
    public String baseUrl() {
        return TestController.baseUrl;
    }

    @Test
    public void postWithInvalidJsonShouldFail() throws Exception {
        String content = "";

        RequestBuilder request = buildJsonRequest(HttpMethod.POST, content)
                .contentType(MediaType.TEXT_PLAIN_VALUE);

        assert4xxResponse(request, HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postWithWrongContentTypeShouldFail() throws Exception {
        RequestBuilder request = buildJsonRequest(HttpMethod.POST, "{}")
                .contentType(MediaType.TEXT_PLAIN_VALUE);

        assert4xxResponse(request, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @Test
    public void getWithWrongPathVariableTypeShouldFail() throws Exception {
        RequestBuilder request = buildRequestWithId(HttpMethod.GET, "wrongIdType")
                .contentType(MediaType.TEXT_PLAIN_VALUE);

        assert4xxResponse(request, HttpStatus.BAD_REQUEST)
                .andExpect(jsonPath("$.detail",
                        equalTo("Failed to convert value of type 'java.lang.String' to required type 'java.lang.Integer'; nested exception is java.lang.NumberFormatException: For input string: \"wrongIdType\"")));
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
