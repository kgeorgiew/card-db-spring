package de.kgeorgiew.carddb.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.kgeorgiew.carddb.domain.Lang;
import de.kgeorgiew.carddb.service.LangRepository;
import de.kgeorgiew.carddb.service.SystemTimeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author kgeorgiew
 *
 *         Create integration tests for all controller endpoints?
 *         Or just a few, to test the json serialization?
 *
 *         Then we could test everything expected the json deserialization and json response.
 *
 *         Create tests:
 *         PUT reqeuest to /api/v1/lang/ with wrong content-type returns a empty 415 (Unsupported Media Type) response
 *
 *         PUT request to /api/v1/lang/ with json data { lang: 'EN' } returns a 400 json response with data
 *         { errors: [{ field: 'lang', message: 'length must be exact 3' }] }
 *
 *         PUT request to /api/v1/lang/ with json data { lang: 'ENGL' } returns a 400 json response with data
 *         { errors: [{ field: 'lang', message: 'length must be exact 3' }] }
 *
 *         PUT request to /api/v1/lang/ with json data { lang: '' } returns a 400 json response with data
 *         { errors: [{ field: 'lang', message: 'may not be null' }] }
 *
 *         PUT request to /api/v1/lang/ with json data {} returns a 400 json response with data
 *         { errors: [{ field: 'lang', message: 'may not be null' }] }
 *
 *         PUT request to /api/v1/lang/ without json data returns a 400 json response with data
 *         { errors: [{ 'Invalid request content' }] }
 *
 *         Put request to /api/v1/lang/ with json data { lang: 'ENG' } returns a 200 json response with data
 *         { lang: 'ENG', created: '2017-01-05T08:55:17.216Z', createdby: 'tester'}
 *
 *         Multiple put request to /api/v1/lang/ with json data { lang: 'ENG' } returns on the second request
 *         a 400 json response with data { errors: [{ field: 'lang', message: 'Entry already exists.' }] }
 *
 *         PUT request to /api/v1/lang/ with json data { lang: 'ENG', notFoundField: 'ENG' } returns a 200 json response
 *         with data  { lang: 'ENG', created: '2017-01-05T08:55:17.216Z', createdby: 'tester'}
 */

@RunWith(SpringRunner.class)
@WebMvcTest(LangController.class)
@ActiveProfiles({"test", "cdi"})
public class LangControllerTest {

    @MockBean
    private LangRepository langRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl = "/api/v1/lang/";
    private String requiredField = "lang";
    private LocalDateTime fixedDateTime;

    @Before
    public void setUp() {
        Clock fixedClock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
        SystemTimeService fixedTimeService = new SystemTimeService(fixedClock);
        fixedDateTime = fixedTimeService.asLocalDateTime();

        //TODO Need to test multiple languages
    }


    @Test
    public void createWithWrongContentTypeShouldFail() throws Exception {
        RequestBuilder request = put(baseUrl).contentType(MediaType.TEXT_PLAIN_VALUE);

        String expectedContent = "";
        mockMvc.perform(request)
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(content().string(expectedContent));
    }

    @Test
    public void createWithShortFieldValueShouldFail() throws Exception {
        String content = "{ \"lang\": \"\"}";

        MockHttpServletRequestBuilder request = getJsonRequest(HttpMethod.PUT).content(content);
        expectError(request, "length must be exact 3")
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors[0].field", equalTo(requiredField)));
    }

    @Test
    public void createWithLongFieldValueShouldFail() throws Exception {
        String content = "{ \"lang\": \"ENGL\"}";

        MockHttpServletRequestBuilder request = getJsonRequest(HttpMethod.PUT).content(content);
        expectError(request, "length must be exact 3")
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors[0].field", equalTo(requiredField)));
    }

    @Test
    public void createWithEmptyJsonShouldFail() throws Exception {
        String content = "{}";

        MockHttpServletRequestBuilder request = getJsonRequest(HttpMethod.PUT).content(content);
        expectError(request, "may not be null")
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void createWithInvalidJsonShouldFail() throws Exception {
        String content = "";
        String expectedMsg = messageSource.getMessage("error.request.body.invalid", null, Locale.getDefault());

        MockHttpServletRequestBuilder request = getJsonRequest(HttpMethod.PUT).content(content);
        expectError(request, expectedMsg)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createShouldReturnJsonWithCreatedFields() throws Exception {
        String content = "{ \"lang\": \"ENG\" }";

        Lang expectedResult = new Lang("ENG", "tester", fixedDateTime);

        Lang lang = new Lang("ENG", null, null);
        given(langRepository.create(lang)).willReturn(expectedResult);

        MockHttpServletRequestBuilder request = getJsonRequest(HttpMethod.PUT).content(content);

        mockMvc.perform(request)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.lang", equalTo(expectedResult.getLang())))
            .andExpect(jsonPath("$.createdBy", equalTo(expectedResult.getCreatedBy())))
            .andExpect(jsonPath("$.created", equalTo(getAsString(expectedResult.getCreated()))));

        verify(langRepository).create(lang);
    }

    @Test
    public void createShouldFailOnDuplicateEntry() throws Exception {
        String content = "{ \"lang\": \"ENG\" }";

        Lang lang = new Lang("ENG", null, null);
        given(langRepository.create(lang)).willThrow(DuplicateKeyException.class);

        String expectedMsg = messageSource.getMessage("error.entity.duplicateEntry", null, Locale.getDefault());

        MockHttpServletRequestBuilder request = getJsonRequest(HttpMethod.PUT).content(content);

        expectError(request, expectedMsg)
                .andExpect(status().isConflict());

        verify(langRepository).create(lang);

    }

    private MockHttpServletRequestBuilder getJsonRequest(HttpMethod method) throws Exception {
        Object urlParams = null;
        MockHttpServletRequestBuilder request = request(method, baseUrl, urlParams)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8);

        return request;
    }

    private ResultActions expectError(MockHttpServletRequestBuilder request, String expectedMsg) throws Exception {
        return mockMvc.perform(request)
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedMsg)));
    }

    private String getAsString(LocalDateTime localDateTime) throws JsonProcessingException {
        return objectMapper.writeValueAsString(localDateTime).replaceAll("\"", "");
    }

}
