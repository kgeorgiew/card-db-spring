package de.kgeorgiew.carddb.web;

import org.junit.Assert;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.hamcrest.Matchers.either;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author kgeorgiew
 */
public interface CrudAssertTrait {

    default ResultActions assert4xxResponse(RequestBuilder request,
                                            HttpStatus status) throws Exception {
        String errorContentType = org.zalando.problem.spring.web.advice.MediaTypes.PROBLEM_VALUE;
        return doRequest(request)
                .andExpect(content().contentTypeCompatibleWith(errorContentType))
                .andExpect(status().is(status.value()))
                .andExpect(jsonPath("$.type").doesNotExist())
                .andExpect(jsonPath("$.title", equalTo(status.getReasonPhrase())))
                .andExpect(jsonPath("$.status", equalTo(status.value())))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    default ResultActions assertValidationError(RequestBuilder request, String expectedField) throws Exception {
        String expectedTitle = "Constraint Violation";
        int expectedStatus = HttpStatus.BAD_REQUEST.value();
        return doRequest(request)
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(status().is(expectedStatus))

                .andExpect(jsonPath("$.type").isNotEmpty())
                .andExpect(jsonPath("$.title", equalTo(expectedTitle)))
                .andExpect(jsonPath("$.status", equalTo(expectedStatus)))
                .andExpect(jsonPath("$.detail").doesNotExist())

                .andExpect(jsonPath("$.violations", hasSize(1)))
                .andExpect(jsonPath("$.violations[0].field", equalTo(expectedField)))
                .andExpect(jsonPath("$.violations[0].message").isNotEmpty());
    }

    default MockHttpServletRequestBuilder buildRequestWithId(HttpMethod method, Object id) {
        Assert.assertThat(method, either(equalTo(HttpMethod.DELETE))
                                    .or(equalTo(HttpMethod.GET))
                                    .or(equalTo(HttpMethod.PUT))
        );
        return request(method, baseUrl() + "/{key}", id)
                .accept(MediaTypes.HAL_JSON, MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8");
    }

    default MockHttpServletRequestBuilder buildJsonRequest(HttpMethod method, String content) throws Exception {
        return buildJsonRequest(method, baseUrl(), content);

    }

    default MockHttpServletRequestBuilder buildJsonRequest(HttpMethod method, String url, String content) throws Exception {
        Assert.assertThat(method, either(equalTo(HttpMethod.POST)).or(equalTo(HttpMethod.PUT)));
        return request(method, url)
                .accept(MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
    }

    default ResultActions assert20xResponse(RequestBuilder request, HttpStatus status) throws Exception {
        return doRequest(request)
                .andExpect(status().is(status.value()))
                .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON));
    }


    String baseUrl();

    ResultActions doRequest(RequestBuilder request) throws Exception;
}
