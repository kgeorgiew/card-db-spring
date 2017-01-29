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
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.*;
import java.util.Locale;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
 *         POST to /api/v1/lang/ with wrong content-type text/plain returns
 *         a empty 415 (Unsupported Media Type) response
 *
 *         POST { lang: "EN" } to /api/v1/lang/ returns a 400 json response with data
 *         { "message": "Validation error", errors: [{ field: "lang", "message:" "length must be exact 3" }] }
 *
 *         POST { lang: "ENGL" } to /api/v1/lang/ returns a 400 json response with data
 *         { "message": "Vallidation error", errors: [{ field: "lang", "message:" "length must be exact 3" }] }
 *
 *         POST {} to /api/v1/lang/ returns a 400 json response with data
 *         { "message": "Vallidation error", errors: [{ field: "lang", "message:" "may not be null" }] }
 *
 *         POST empty request to /api/v1/lang/ returns a 400 json response with data
 *         { "message:" "Invalid request content" }
 *
 *         POST empty request to /api/v1/lang/ with Accept-Language: de returns a 400 json response with data
 *         { "message:" "Ungültige Anfrage }
 *
 *         POST { lang: "ENG" } to /api/v1/lang/ returns a 200 json response with data
 *         { lang: "ENG", created: "2017-01-05T08:55:17.216Z", createdby: "tester"}
 *
 *         Successful POST request should return { ..., "_links": { "self": { href: "/api/v1/lang/1" } } }
 *
 *         POST { lang: "ENG", unexpectedField: "unexpected" } to /api/v1/lang/ returns a 200 json response
 *         with data  { lang: "ENG", created: "2017-01-05T08:55:17.216Z", createdby: "tester"}
 *
 *         POST { lang: "ENG" } multiple times to /api/v1/lang/ returns a 400 json response with data
 *         { "message:" "Entry already exists." }
 *
 *
 *         Delete tests:
 *         DELETE to /api/v1/lang/1 returns a 200 empty response without data
 *
 *         DELETE to /api/va/lang/2 returns a 404 json response with data
 *         { "message": "Not found" }
 *
 *         DELETE to /api/v1/lang/1 with content-type 'text/plain' returns a 200 empty response
 *
 *
 *         Put/Update tests:
 *
 *
 *
 *
 */

@RunWith(SpringRunner.class)
@WebMvcTest(LangController.class)
@ActiveProfiles({"test"})
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
    private ZonedDateTime fixedDateTime;

    @Before
    public void setUp() {
        Clock fixedClock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
        SystemTimeService fixedTimeService = new SystemTimeService(fixedClock);
        fixedDateTime = fixedTimeService.asZonedDateTime();
    }

    @Test
    public void createWithWrongContentTypeShouldFail() throws Exception {
        RequestBuilder request = post(baseUrl).contentType(MediaType.TEXT_PLAIN_VALUE);

        String expectedContent = "";
        mockMvc.perform(request)
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(content().string(expectedContent));
    }

    @Test
    public void createWithShortFieldValueShouldFail() throws Exception {
        String content = "{ \"lang\": \"\"}";

        MockHttpServletRequestBuilder request = jsonRequest(HttpMethod.POST).content(content);

        assertExpectedError(request, "Validation error")
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors[0].field",
                        equalTo(requiredField)))
                .andExpect(jsonPath("$.errors[0].message",
                        equalTo("length must be exact 3")));
    }

    @Test
    public void createWithLongFieldValueShouldFail() throws Exception {
        String content = "{ \"lang\": \"ENGL\"}";

        MockHttpServletRequestBuilder request = jsonRequest(HttpMethod.POST).content(content);

        assertExpectedError(request, "Validation error")
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors[0].field",
                        equalTo(requiredField)))
                .andExpect(jsonPath("$.errors[0].message",
                        equalTo("length must be exact 3")));
    }

    @Test
    public void createWithEmptyJsonShouldFail() throws Exception {
        String content = "{}";

        MockHttpServletRequestBuilder request = jsonRequest(HttpMethod.POST).content(content);

        assertExpectedError(request, "Validation error")
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors[0].message",
                        equalTo("may not be null")))
                .andExpect(jsonPath("$.errors[0].field",
                        equalTo(requiredField)));

    }


    @Test
    public void createWithInvalidJsonShouldFail() throws Exception {
        String content = "";
        String expectedMsg = messageSource.getMessage("error.request.body.invalid", null, Locale.getDefault());

        MockHttpServletRequestBuilder request = jsonRequest(HttpMethod.POST).content(content);

        assertExpectedError(request, expectedMsg)
                .andExpect(status().isBadRequest());
    }


    @Test
    public void shouldReturnGermanErrorMessage() throws Exception {
        Locale expectedLocal = Locale.GERMAN;
        String content = "";
        String expectedMsg = messageSource.getMessage("error.request.body.invalid",
                null, expectedLocal);

        MockHttpServletRequestBuilder request = jsonRequest(HttpMethod.POST)
                .locale(expectedLocal)
                .content(content);

        assertExpectedError(request, expectedMsg)
                .andExpect(status().isBadRequest());

    }

    @Test
    public void createShouldReturnJsonWithCreatedFields() throws Exception {
        Lang expectedResult = new Lang("ENG", "tester", fixedDateTime);
        Lang lang = new Lang("ENG", null, null);

        given(langRepository.create(lang)).willReturn(expectedResult);

        String content = "{ \"lang\": \"ENG\", \"unexpectedField\": \"unexpected\" }";

        MockHttpServletRequestBuilder request = jsonRequest(HttpMethod.POST).content(content);

        String expecedCreated = getAsString(expectedResult.getCreated());
        mockMvc.perform(request)
            .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.lang", equalTo(expectedResult.getLang())))
            .andExpect(jsonPath("$.createdBy", equalTo(expectedResult.getCreatedBy())))
            .andExpect(jsonPath("$.created", equalTo(expecedCreated)))

            // Expected links
//            .andExpect(jsonPath("$._links.profile").exists())
            .andExpect(jsonPath("$._links.self.href").isString())
            .andExpect(jsonPath("$._links.delete.href").isString())
            .andExpect(jsonPath("$._links.update.href").isString());

        verify(langRepository).create(lang);
    }

    @Test
    public void createShouldFailOnDuplicateEntry() throws Exception {
        String content = "{ \"lang\": \"ENG\" }";

        Lang lang = new Lang("ENG", null, null);
        given(langRepository.create(lang)).willThrow(DuplicateKeyException.class);

        String expectedMsg = messageSource.getMessage("error.entity.duplicateEntry", null, Locale.getDefault());

        MockHttpServletRequestBuilder request = jsonRequest(HttpMethod.POST).content(content);

        assertExpectedError(request, expectedMsg)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors").doesNotExist());

        verify(langRepository).create(lang);

    }

    private MockHttpServletRequestBuilder jsonRequest(HttpMethod method) throws Exception {
        Object urlParams = null;
        MockHttpServletRequestBuilder request = request(method, baseUrl, urlParams)
                .accept(MediaTypes.HAL_JSON_VALUE, "application/alps+json")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON);

        return request;
    }

    private ResultActions assertExpectedError(MockHttpServletRequestBuilder request, String expectedMsg) throws Exception {
        return mockMvc.perform(request)
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$.message", equalTo(expectedMsg)));
    }

    private String getAsString(ZonedDateTime dateTime) throws JsonProcessingException {
        return objectMapper.writeValueAsString(dateTime).replaceAll("\"", "");
    }

}
