package de.kgeorgiew.carddb.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.kgeorgiew.carddb.ConstrainedFields;
import de.kgeorgiew.carddb.domain.Lang;
import de.kgeorgiew.carddb.service.LangRepository;
import de.kgeorgiew.carddb.service.MessagesService;
import de.kgeorgiew.carddb.service.time.FixedTimeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.hypermedia.LinksSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.ZonedDateTime;
import java.util.Optional;

import static de.kgeorgiew.carddb.HalRestAsserts.*;
import static de.kgeorgiew.carddb.RestRequestUtil.jsonRequest;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author kgeorgiew
 */
@RunWith(SpringRunner.class)
@WebMvcTest(LangController.class)
@AutoConfigureRestDocs("target/generated-snippets/lang")
public class LangControllerTest {
//
//    @Rule
//    public JUnitRestDocumentation restDocumentation =
//            new JUnitRestDocumentation("target/generated-snippets/lang");

    @MockBean
    private LangRepository langRepository;

    @Autowired
    private MessagesService messages;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String baseUrl = "/api/v1/lang";
    private final String urlWithId = baseUrl + "/{lang}";

    private final String expectedErrorField = "lang.lang";

    private String prePersistLangJson;

    private Lang prePersistLang;
    private Lang persistedLang;

    private Lang preUpdateLang;
    private Lang updateLang;

    private final LinksSnippet interactionLinks = links(halLinks(),
            linkWithRel("self").description("Self link"),
            linkWithRel("ex:delete").description("Link to delete the resource <resources-lang-delete,Delete>"),
            linkWithRel("ex:update").description("Link to update the resource <resources-lang-update,Update>"),
            linkWithRel("curies").description("Extension link description ")
    );

    private final ResponseFieldsSnippet createResponseFields = responseFields(
            fieldWithPath("lang").description("The language"),
            fieldWithPath("createdBy").description("The creation timestamp"),
            fieldWithPath("created").description("The creation user"),
            fieldWithPath("_links").description("<<resources-lang-links,Links>> to other resources")
    );

    @Before
    public void setUp() {
//        langRepository = mock(LangRepository.class);
//
//        final LangController controller = new LangController(langRepository, new LangResourceAssembler());
//        objectMapper = new ObjectMapper().registerModules(
//                new ProblemModule(),
//                new ConstraintViolationProblemModule(),
//                new JavaTimeModule(),
//                new Jdk8Module(),
//                new Jackson2HalModule()
//        );
//        AppConfig appConfig = new AppConfig();
//
//        objectMapper.setHandlerInstantiator(
//                new Jackson2HalModule.HalHandlerInstantiator(new EvoInflectorRelProvider(), appConfig.curieProvider(), null));
//        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
//
//        mvc = MockMvcBuilders.standaloneSetup(controller)
//                .apply(documentationConfiguration(this.restDocumentation))
//                .setControllerAdvice(new ControllerExceptionHandler())
//                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
//                .build();


        ZonedDateTime fixedDateTime = new FixedTimeService().asZonedDateTime();

        this.prePersistLangJson = "{ \"lang\": \"ENG\" }";
        this.prePersistLang = new Lang("ENG", null, null);
        this.persistedLang = new Lang("ENG", "tester", fixedDateTime);

        this.preUpdateLang = new Lang("FRA", persistedLang.getCreatedBy(), persistedLang.getCreated());
        this.updateLang = new Lang(preUpdateLang.getLang(),
                preUpdateLang.getCreatedBy(),
                preUpdateLang.getCreated(),
                "updater",
                ZonedDateTime.now());
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
//        assertJsonError(request, HttpStatus.BAD_REQUEST);
//    }

    @Test
    public void createShouldReturn400OnValidationError() throws Exception {
        String inputContent = "{}";

        ResultActions actualResponse = mvc.perform(jsonRequest(post(baseUrl), inputContent));

        assertJsonValidationError(actualResponse, expectedErrorField);
    }

    /**
     * Test the happy path and generate snippets for lang post
     *
     */
    @Test
    public void createShouldReturnJsonWithAllFieldsSet() throws Exception {
        given(langRepository.create(prePersistLang)).willReturn(persistedLang);

        ResultActions actualResponse = mvc.perform(jsonRequest(post(baseUrl), prePersistLangJson));
        HttpStatus expectedStatus = HttpStatus.CREATED;

        assertJsonResponse(actualResponse, expectedStatus);
        assertExpectedContent(actualResponse, persistedLang);

        doDocs(actualResponse,
                "post",
                interactionLinks,
                requestFields(
                        new ConstrainedFields(Lang.class).withPath("lang").description("The language")
                ),
                createResponseFields
        );

        verify(langRepository).create(prePersistLang);
    }

    @Test
    public void createShouldReturn409OnDuplicateEntry() throws Exception {
        String expectedMsg = "Entry already exists";

        given(langRepository.create(prePersistLang)).willThrow(new DuplicateKeyException(expectedMsg));

        ResultActions actualResponse = mvc.perform(jsonRequest(post(baseUrl), prePersistLangJson));
        HttpStatus expectedStatus = HttpStatus.CONFLICT;

        assertJsonError(actualResponse, expectedStatus, expectedMsg);

        verify(langRepository).create(prePersistLang);
    }

    /**
     * Test the happy path and generate snippets for lang get
     */
    @Test
    public void getShouldReturnOneEntryById() throws Exception {
        String inputLang = updateLang.getLang();

        given(langRepository.get(inputLang)).willReturn(Optional.of(updateLang));

        ResultActions actualResponse = mvc.perform(jsonRequest(get(urlWithId, inputLang)));
        HttpStatus expectedStatus = HttpStatus.OK;

        assertJsonResponse(actualResponse, expectedStatus);
        assertExpectedContent(actualResponse, updateLang);

        actualResponse.andDo(document("get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        interactionLinks,
                        pathParameters(
                                parameterWithName("lang").description("The language")
                        ),
                        createResponseFields.and(
                                fieldWithPath("updatedBy").optional().description("Last update user"),
                                fieldWithPath("updated").optional().description("Last update timestamp")
                        )

                ));

        verify(langRepository).get(inputLang);
    }

    @Test
    public void getShouldReturn404OnUnknownId() throws Exception {
        String inputLang = prePersistLang.getLang();
        String expectedMessage = messages.getMessage("error.entity.notFound", inputLang);

        given(langRepository.get(inputLang)).willReturn(Optional.empty());

        ResultActions actualResponse = mvc.perform(jsonRequest(get(urlWithId, inputLang)));
        HttpStatus expectedStatus = HttpStatus.NOT_FOUND;

        assertJsonError(actualResponse, expectedStatus, expectedMessage);

        verify(langRepository).get(inputLang);
    }

    /**
     * Test the happy path and generate snippets for lang delete
     */
    @Test
    public void deleteShouldReturnEmpty200OnSuccess() throws Exception {
        String inputLang = persistedLang.getLang();

        doNothing().when(langRepository).delete(inputLang);

        ResultActions actualResponse = mvc.perform(jsonRequest(delete(urlWithId, inputLang)));
        HttpStatus expectedStatus = HttpStatus.OK;
        String expectedContent = "";

        actualResponse
            .andExpect(status().is(expectedStatus.value()))
            .andExpect(content().string(expectedContent));

        actualResponse.andDo(document("delete",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                        parameterWithName("lang").description("The language")
                )
        ));

        verify(langRepository).delete(inputLang);
    }

    @Test
    public void deleteShouldReturn404IfNotFound() throws Exception {
        String inputLang = prePersistLang.getLang();

        doThrow(new DataRetrievalFailureException("Some error text")).when(langRepository).delete(inputLang);

        ResultActions actualResponse = mvc.perform(jsonRequest(delete(urlWithId, inputLang)));
        HttpStatus expectedStatus = HttpStatus.NOT_FOUND;

        assertJsonError(actualResponse, expectedStatus);

        verify(langRepository).delete(inputLang);
    }

    @Test
    public void updateShouldReturnValidationError() throws Exception {
        String inputContent = "{}";
        String inputLang = "ENG";

        ResultActions actualResponse = mvc.perform(jsonRequest(put(urlWithId, inputLang), inputContent));

        assertJsonValidationError(actualResponse, expectedErrorField);
    }

    @Test
    public void updateShouldReturn400WithDifferentPathAndEntityId() throws Exception {
        String inputLang = "DEU";
        String inputContent = prePersistLangJson;

        ResultActions actualResponse = mvc.perform(jsonRequest(put(urlWithId, inputLang), inputContent));
        HttpStatus expectedStatus = HttpStatus.BAD_REQUEST;
        String expectedMessage = messages.getMessage("error.url.parameter.mismatch", inputLang, prePersistLang.getLang());

        assertJsonError(actualResponse, expectedStatus, expectedMessage);
    }

    @Test
    public void updateShouldReturn404IfNotFound() throws Exception {
        String inputLang = preUpdateLang.getLang();
        String inputContent = objectMapper.writeValueAsString(preUpdateLang);

        given(langRepository.update(preUpdateLang)).willThrow(new DataRetrievalFailureException("Some error text"));

        ResultActions actualResponse = mvc.perform(jsonRequest(put(urlWithId, inputLang), inputContent));
        HttpStatus expectedStatus = HttpStatus.BAD_REQUEST;

        assertJsonError(actualResponse, expectedStatus);

        verify(langRepository).update(preUpdateLang);
    }

    /**
     * Test the happy path and generate snippets for lang put
     */
    @Test
    public void updateShouldSuccess() throws Exception {
        String inputLang = preUpdateLang.getLang();
        String inputContent = objectMapper.writeValueAsString(preUpdateLang);

        given(langRepository.update(preUpdateLang)).willReturn(updateLang);

        ResultActions actualResponse = mvc.perform(jsonRequest(put(urlWithId, inputLang), inputContent));
        HttpStatus expectedStatus = HttpStatus.OK;

        assertJsonResponse(actualResponse, expectedStatus);
        assertExpectedContent(actualResponse, updateLang);

        actualResponse.andDo(document("put",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                interactionLinks,
                pathParameters(
                        parameterWithName("lang").description("The language")
                ),
                createResponseFields.and(
                        fieldWithPath("updatedBy").description("Last put user"),
                        fieldWithPath("updated").description("Last put timestamp")
                )
        ));

        verify(langRepository).update(preUpdateLang);
    }

    private ResultActions assertExpectedContent(ResultActions actions, Lang expectedEntry) throws Exception {
        return actions
                .andExpect(jsonPath("$.lang", equalTo(expectedEntry.getLang())))
                .andExpect(jsonPath("$.created", equalTo(getAsString(expectedEntry.getCreated()))))
                .andExpect(jsonPath("$.createdBy", equalTo(expectedEntry.getCreatedBy())));
    }

    private String getAsString(ZonedDateTime dateTime) throws JsonProcessingException {
        return objectMapper.writeValueAsString(dateTime).replaceAll("\"", "");
    }

}
