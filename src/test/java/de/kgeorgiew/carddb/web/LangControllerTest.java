package de.kgeorgiew.carddb.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.kgeorgiew.carddb.ConstrainedFields;
import de.kgeorgiew.carddb.domain.Lang;
import de.kgeorgiew.carddb.service.LangRepository;
import de.kgeorgiew.carddb.service.SystemTimeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;
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
@AutoConfigureRestDocs("target/generated-snippets")
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
    private ZonedDateTime fixedDateTime;

    @Before
    public void setUp() {
        Clock fixedClock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
        SystemTimeService fixedTimeService = new SystemTimeService(fixedClock);
        fixedDateTime = fixedTimeService.asZonedDateTime();
    }

    @Test
    public void createWithWrongContentTypeShouldFail() throws Exception {
        MockHttpServletRequestBuilder request = post(baseUrl).contentType(MediaType.TEXT_PLAIN_VALUE);

        assert4xxError(request, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    //TODO This is testing the spring behavior. It should be same for all controllers, so outsource this code?
    @Test
    public void createWithInvalidJsonShouldFail() throws Exception {
        String content = "";

        MockHttpServletRequestBuilder request = jsonRequest(HttpMethod.POST).content(content);

        //TODO check message
        assert4xxError(request, HttpStatus.BAD_REQUEST);
    }

    //TODO This is testing the spring behavior. It should be same for all controllers, so outsource this code?
    @Test
    public void createShouldIgnoreUnknownFields() throws Exception {
        Lang expectedResult = new Lang("ENG", "tester", fixedDateTime);
        Lang lang = new Lang("ENG", null, null);

        given(langRepository.create(lang)).willReturn(expectedResult);

        String content = "{ \"lang\": \"ENG\", \"unknownField\": \"test\" }";

        MockHttpServletRequestBuilder request = jsonRequest(HttpMethod.POST).content(content);

        mockMvc.perform(request)
                .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.unknownField").doesNotExist());
    }

//    @Test
//    public void shouldReturnGermanErrorMessage() throws Exception {
//        Locale expectedLocal = Locale.GERMAN;
//        String content = "";
//        String expectedMsg = messageSource.getMessage("error.request.body.invalid",
//                null, expectedLocal);
//
//        MockHttpServletRequestBuilder request = jsonRequest(HttpMethod.POST)
//                .locale(expectedLocal)
//                .content(content);
//
//
//        assert4xxError(request, HttpStatus.BAD_REQUEST);
//    }


    @Test
    public void shouldReturnAnValidationError() throws Exception {
        String content = "{}";

        MockHttpServletRequestBuilder request = jsonRequest(HttpMethod.POST).content(content);

        String expectedTitle = "Constraint Violation";
        int expectedStatus = HttpStatus.BAD_REQUEST.value();
        String expectedField = "lang.lang";

        mockMvc.perform(request)
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

    @Test
    public void createShouldReturnJsonWithCreatedFields() throws Exception {
        Lang expectedResult = new Lang("ENG", "tester", fixedDateTime);
        Lang lang = new Lang("ENG", null, null);

        given(langRepository.create(lang)).willReturn(expectedResult);

        Map<String, Object> content = new HashMap<>();
        content.put("lang", "ENG");

        MockHttpServletRequestBuilder request = jsonRequest(HttpMethod.POST).content(objectMapper.writeValueAsString(content));

        String expectedCreated = getAsString(expectedResult.getCreated());

        mockMvc.perform(request)
            .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.lang", equalTo(expectedResult.getLang())))
            .andExpect(jsonPath("$.createdBy", equalTo(expectedResult.getCreatedBy())))
            .andExpect(jsonPath("$.created", equalTo(expectedCreated)))

            .andDo(document("langCreate",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                links(halLinks(),
                    linkWithRel("self").description("Self link"),
                    linkWithRel("ex:delete").description("Link to delete the resource"),
                    linkWithRel("ex:update").description("Link to update the resource <resources-update,update>"),
                    linkWithRel("curies").description("Extension link description ")
                ),
                requestFields(
                        attributes(key("title").value("Request fields")),
                        new ConstrainedFields(Lang.class).withPath("lang").description("The language")
                ),
                responseFields(
                        fieldWithPath("lang").description("The language"),
                        fieldWithPath("createdBy").description("The creation timestamp"),
                        fieldWithPath("created").description("The creation user"),
                        fieldWithPath("_links").description("<<resources-tag-links,Links>> to other resources")
                )
            ));

        verify(langRepository).create(lang);
    }

    @Test
    public void createShouldFailOnDuplicateEntry() throws Exception {
        String content = "{ \"lang\": \"ENG\" }";
        String expectedMsg = messageSource.getMessage("error.entity.duplicateEntry", null, Locale.getDefault());

        Lang lang = new Lang("ENG", null, null);

        given(langRepository.create(lang)).willThrow(new DuplicateKeyException(expectedMsg));

        MockHttpServletRequestBuilder request = jsonRequest(HttpMethod.POST).content(content);

        assert4xxError(request, HttpStatus.CONFLICT);

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

    private ResultActions assert4xxError(MockHttpServletRequestBuilder request,
                                         HttpStatus status) throws Exception {
        int expectedStatus = status.value();
        String expectedTitle = status.getReasonPhrase();
        return mockMvc.perform(request)
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(status().is(expectedStatus))

                .andExpect(jsonPath("$.type").doesNotExist())
                .andExpect(jsonPath("$.title", equalTo(expectedTitle)))
                .andExpect(jsonPath("$.status", equalTo(expectedStatus)))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    private String getAsString(ZonedDateTime dateTime) throws JsonProcessingException {
        return objectMapper.writeValueAsString(dateTime).replaceAll("\"", "");
    }

}
