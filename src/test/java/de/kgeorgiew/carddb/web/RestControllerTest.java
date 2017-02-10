package de.kgeorgiew.carddb.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author kgeorgiew
 */
@RunWith(SpringRunner.class)
@WebMvcTest(TestController.class)
public class RestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final String baseUrl = "/test";
    private String errorContentType = org.zalando.problem.spring.web.advice.MediaTypes.PROBLEM_VALUE;

    @Test
    public void postWithInvalidJsonShouldFail() throws Exception {
        String content = "";

        HttpStatus status = HttpStatus.BAD_REQUEST;

        mockMvc.perform(
                post(baseUrl)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        ).andExpect(content().contentTypeCompatibleWith(errorContentType))
                .andExpect(status().is(status.value()))
                .andExpect(jsonPath("$.type").doesNotExist())
                .andExpect(jsonPath("$.title", equalTo(status.getReasonPhrase())))
                .andExpect(jsonPath("$.status", equalTo(status.value())))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    public void requestWithWrongContentTypeShouldFail() throws Exception {
        HttpStatus status = HttpStatus.UNSUPPORTED_MEDIA_TYPE;

        mockMvc.perform(
                post(baseUrl)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.TEXT_PLAIN_VALUE)
        ).andExpect(content().contentTypeCompatibleWith(errorContentType))
                .andExpect(status().is(status.value()))
                .andExpect(jsonPath("$.type").doesNotExist())
                .andExpect(jsonPath("$.title", equalTo(status.getReasonPhrase())))
                .andExpect(jsonPath("$.status", equalTo(status.value())))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }
}
