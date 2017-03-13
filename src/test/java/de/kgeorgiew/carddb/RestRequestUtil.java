package de.kgeorgiew.carddb;

import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/**
 * @author kgeorgiew
 */
public class RestRequestUtil {


    public static MockHttpServletRequestBuilder addJsonContent(MockHttpServletRequestBuilder requestBuilder, String content) throws Exception {
        return addAcceptJson(requestBuilder)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
    }

    public static MockHttpServletRequestBuilder addAcceptJson(MockHttpServletRequestBuilder requestBuilder) {
        return requestBuilder
                .accept(MediaTypes.HAL_JSON, MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8");
    }
}
