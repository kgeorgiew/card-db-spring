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
public class RestRequestUtil {


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
