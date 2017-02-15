package de.kgeorgiew.carddb;

import org.junit.Assert;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author kgeorgiew
 */
public class HalRestAsserts {

    public static final String errorContentType = org.zalando.problem.spring.web.advice.MediaTypes.PROBLEM_VALUE;

    public static ResultActions assertValidationError(ResultActions actions, String expectedField) throws Exception {
        String expectedTitle = "Constraint Violation";
        int expectedStatus = HttpStatus.BAD_REQUEST.value();
        return actions
                .andExpect(content().contentTypeCompatibleWith(errorContentType))
                .andExpect(status().is(expectedStatus))

                .andExpect(jsonPath("$.type").isNotEmpty())
                .andExpect(jsonPath("$.title", equalTo(expectedTitle)))
                .andExpect(jsonPath("$.status", equalTo(expectedStatus)))
                .andExpect(jsonPath("$.detail").doesNotExist())

                .andExpect(jsonPath("$.violations", hasSize(1)))
                .andExpect(jsonPath("$.violations[0].field", equalTo(expectedField)))
                .andExpect(jsonPath("$.violations[0].message").isNotEmpty());
    }


    public static ResultActions assertError(ResultActions actions, HttpStatus status, String message) throws Exception {
        return assertError(actions, status)
                .andExpect(jsonPath("$.detail", equalTo(message)));
    }

    public static ResultActions assertError(ResultActions actions, HttpStatus status) throws Exception {
        return actions
                .andExpect(content().contentTypeCompatibleWith(errorContentType))
                .andExpect(status().is(status.value()))
                .andExpect(jsonPath("$.type").doesNotExist())
                .andExpect(jsonPath("$.title", equalTo(status.getReasonPhrase())))
                .andExpect(jsonPath("$.status", equalTo(status.value())))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    public static ResultActions assertSuccess(ResultActions actions, HttpStatus status) throws Exception {
        Assert.assertThat(status, either(equalTo(HttpStatus.OK)).or(equalTo(HttpStatus.CREATED)));

        return actions.andExpect(status().is(status.value()))
                .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON));
    }

    public static MockHttpServletRequestBuilder jsonRequest(MockHttpServletRequestBuilder requestBuilder, String content) throws Exception {
        return jsonRequest(requestBuilder)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
    }

    public static MockHttpServletRequestBuilder jsonRequest(MockHttpServletRequestBuilder requestBuilder) {
        return requestBuilder
                .accept(MediaTypes.HAL_JSON, MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8");
    }
}
