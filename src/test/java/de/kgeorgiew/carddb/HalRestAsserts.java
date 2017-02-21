package de.kgeorgiew.carddb;

import org.junit.Assert;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author kgeorgiew
 */
public class HalRestAsserts {

    private static final String errorContentType = org.zalando.problem.spring.web.advice.MediaTypes.PROBLEM_VALUE;

    public static ResultActions assertJsonResponse(ResultActions actualResponse, HttpStatus expectedStatus) throws Exception {
        Assert.assertThat(expectedStatus, either(equalTo(HttpStatus.OK)).or(equalTo(HttpStatus.CREATED)));

        return actualResponse
                .andExpect(status().is(expectedStatus.value()))
                .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON));
    }

    public static ResultActions assertJsonValidationError(ResultActions actions, String expectedErrorField) throws Exception {
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
                .andExpect(jsonPath("$.violations[0].field", equalTo(expectedErrorField)))
                .andExpect(jsonPath("$.violations[0].message").isNotEmpty());
    }

    public static ResultActions assertJsonError(ResultActions actualResponse, HttpStatus exptectedStatus, String expectedErrorMsg) throws Exception {
        return assertJsonError(actualResponse, exptectedStatus)
                .andExpect(jsonPath("$.detail", equalTo(expectedErrorMsg)));
    }

    public static ResultActions assertJsonError(ResultActions actualResponse, HttpStatus expectedStatus) throws Exception {
        return actualResponse
                .andExpect(content().contentTypeCompatibleWith(errorContentType))
                .andExpect(status().is(expectedStatus.value()))
                .andExpect(jsonPath("$.type").doesNotExist())
                .andExpect(jsonPath("$.title", equalTo(expectedStatus.getReasonPhrase())))
                .andExpect(jsonPath("$.status", equalTo(expectedStatus.value())))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    public static ResultActions doDocs(ResultActions response, String name, Snippet... snippets) throws Exception {
        return response.andDo(
                document(name,
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    snippets)
        );
    }

}
