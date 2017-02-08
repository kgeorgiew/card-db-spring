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
import org.springframework.restdocs.hypermedia.LinksSnippet;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.request;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author kgeorgiew
 *
 */
@RunWith(SpringRunner.class)
@WebMvcTest(LangController.class)
@ActiveProfiles({"test"})
@AutoConfigureRestDocs("target/generated-snippets/lang")
public class LangControllerTest {

    @MockBean
    private LangRepository langRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ObjectMapper objectMapper;

    private final LinksSnippet interactionLinks = links(halLinks(),
            linkWithRel("self").description("Self link"),
            linkWithRel("ex:delete").description("Link to delete the resource <resources-lang-delete,Delete>"),
            linkWithRel("ex:update").description("Link to update the resource <resources-lang-update,Update>"),
            linkWithRel("curies").description("Extension link description ")
    );

    private final ResponseFieldsSnippet responseFields = responseFields(
            fieldWithPath("lang").description("The language"),
            fieldWithPath("createdBy").description("The creation timestamp"),
            fieldWithPath("created").description("The creation user"),
            fieldWithPath("_links").description("<<resources-lang-links,Links>> to other resources")
    );

    private String baseUrl = "/api/v1/lang";
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

        assert4xxError(request, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    //TODO This is testing the spring behavior. It should be same for all controllers, so outsource this code?
    @Test
    public void createWithInvalidJsonShouldFail() throws Exception {
        String content = "";

        RequestBuilder request = jsonRequest(HttpMethod.POST, content);

        assert4xxError(request, HttpStatus.BAD_REQUEST);
    }

    //TODO This is testing the spring behavior. It should be same for all controllers, so outsource this code?
    @Test
    public void createShouldIgnoreUnknownFields() throws Exception {
        Lang expectedResult = new Lang("ENG", "tester", fixedDateTime);
        Lang lang = new Lang("ENG", null, null);

        given(langRepository.create(lang)).willReturn(expectedResult);

        String content = "{ \"lang\": \"ENG\", \"unknownField\": \"test\" }";

        RequestBuilder request = jsonRequest(HttpMethod.POST, content);

        assert20xWithEntry(request, HttpStatus.CREATED, expectedResult)
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

        RequestBuilder request = jsonRequest(HttpMethod.POST, content);

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

        RequestBuilder request = jsonRequest(HttpMethod.POST, objectMapper.writeValueAsString(content));

        assert20xWithEntry(request, HttpStatus.CREATED, expectedResult)
                .andDo(document("create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        interactionLinks,
                        requestFields(
                                new ConstrainedFields(Lang.class).withPath("lang").description("The language")
                        ),
                        responseFields
                ));

        verify(langRepository).create(lang);
    }

    @Test
    public void createShouldFailOnDuplicateEntry() throws Exception {
        String content = "{ \"lang\": \"ENG\" }";
        String expectedMsg = "Entry already exists";

        Lang lang = new Lang("ENG", null, null);

        given(langRepository.create(lang)).willThrow(new DuplicateKeyException(expectedMsg));

        RequestBuilder request = jsonRequest(HttpMethod.POST, content);

        assert4xxError(request, HttpStatus.CONFLICT);

        verify(langRepository).create(lang);
    }

    @Test
    public void getShouldReturnOneEntryById() throws Exception {
        String inputLang = "ENG";
        String requestUrl = baseUrl  + "/{lang}";
        Lang expectedResult = new Lang(inputLang, "tester", fixedDateTime);

        given(langRepository.get(inputLang)).willReturn(Optional.of(expectedResult));

        RequestBuilder request = request(HttpMethod.GET, requestUrl, inputLang)
                .accept(MediaTypes.HAL_JSON, MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8");

        assert20xWithEntry(request, HttpStatus.OK, expectedResult)
                .andDo(document("get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        interactionLinks,
                        pathParameters(
                                parameterWithName("lang").description("The language")
                        ),
                        responseFields
                ));

        verify(langRepository).get(inputLang);
    }

    @Test
    public void getShouldReturnNotFound() throws Exception {
        String inputLang = "DEU";
        String requestUrl = baseUrl  + "/{lang}";
        String expectedMessage = "Entry for " + inputLang + " not found";

        given(langRepository.get(inputLang)).willReturn(Optional.empty());

        RequestBuilder request = request(HttpMethod.GET, requestUrl, inputLang)
                .accept(MediaTypes.HAL_JSON, MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8");

        assert4xxError(request, HttpStatus.NOT_FOUND)
                .andExpect(jsonPath("$.detail", equalTo(expectedMessage)));

        verify(langRepository).get(inputLang);

    }

    private MockHttpServletRequestBuilder jsonRequest(HttpMethod method, String content) throws Exception {
        return jsonRequest(method).content(content);
    }

    private MockHttpServletRequestBuilder jsonRequest(HttpMethod method) throws Exception {
        return request(method, baseUrl)
                .accept(MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON);
    }

    private ResultActions assert20xWithEntry(RequestBuilder request, HttpStatus status, Lang expectedResult) throws Exception {
        return mockMvc.perform(request)
                .andExpect(status().is(status.value()))
                .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$.lang", equalTo(expectedResult.getLang())))
                .andExpect(jsonPath("$.created", equalTo(getAsString(expectedResult.getCreated()))))
                .andExpect(jsonPath("$.createdBy", equalTo(expectedResult.getCreatedBy())));
    }

    private ResultActions assert4xxError(RequestBuilder request,
                                         HttpStatus status) throws Exception {
        String errorContentType = org.zalando.problem.spring.web.advice.MediaTypes.PROBLEM_VALUE;
        return mockMvc.perform(request)
                .andExpect(content().contentTypeCompatibleWith(errorContentType))
                .andExpect(status().is(status.value()))
                .andExpect(jsonPath("$.type").doesNotExist())
                .andExpect(jsonPath("$.title", equalTo(status.getReasonPhrase())))
                .andExpect(jsonPath("$.status", equalTo(status.value())))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    private String getAsString(ZonedDateTime dateTime) throws JsonProcessingException {
        return objectMapper.writeValueAsString(dateTime).replaceAll("\"", "");
    }

}
