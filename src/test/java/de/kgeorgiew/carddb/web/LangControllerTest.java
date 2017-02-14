package de.kgeorgiew.carddb.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.kgeorgiew.carddb.ConstrainedFields;
import de.kgeorgiew.carddb.domain.Lang;
import de.kgeorgiew.carddb.service.LangRepository;
import de.kgeorgiew.carddb.service.MessagesService;
import de.kgeorgiew.carddb.service.SystemTimeService;
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
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.request;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author kgeorgiew
 */
@RunWith(SpringRunner.class)
@WebMvcTest(LangController.class)
@AutoConfigureRestDocs("target/generated-snippets/lang")
public class LangControllerTest implements CrudAssertTrait {
//
//    @Rule
//    public JUnitRestDocumentation restDocumentation =
//            new JUnitRestDocumentation("target/generated-snippets/lang");

    @MockBean
    private LangRepository langRepository;

    @Autowired
    private MessagesService messages;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String requiredLangField = "lang.lang";
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
//        mockMvc = MockMvcBuilders.standaloneSetup(controller)
//                .apply(documentationConfiguration(this.restDocumentation))
//                .setControllerAdvice(new ControllerExceptionHandler())
//                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
//                .build();

        Clock fixedClock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
        ZonedDateTime fixedDateTime = new SystemTimeService(fixedClock).asZonedDateTime();

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

    @Override
    public String baseUrl() {
        return "/api/v1/lang";
    }

    @Override
    public String urlWithId() {
        return baseUrl() + "/{lang}";
    }

    @Override
    public ResultActions performRequest(RequestBuilder request) throws Exception {
        return mockMvc.perform(request);
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
//        assertError(request, HttpStatus.BAD_REQUEST);
//    }

    @Test
    public void createShouldReturnAnValidationError() throws Exception {
        String inputContent = "{}";

        assertValidationError(post(inputContent), requiredLangField);

    }

    /**
     * Test the happy path and generate snippets for lang post
     *
     */
    @Test
    public void createShouldReturnJsonWithAllFieldsSet() throws Exception {
        given(langRepository.create(prePersistLang)).willReturn(persistedLang);

        assert20xWithEntry(post(prePersistLangJson), HttpStatus.CREATED, persistedLang)
                .andDo(document("post",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        interactionLinks,
                        requestFields(
                                new ConstrainedFields(Lang.class).withPath("lang").description("The language")
                        ),
                        createResponseFields
                ));

        verify(langRepository).create(prePersistLang);
    }

    @Test
    public void createShouldFailOnDuplicateEntry() throws Exception {
        String expectedMsg = "Entry already exists";

        given(langRepository.create(prePersistLang)).willThrow(new DuplicateKeyException(expectedMsg));

        assertError(post(prePersistLangJson), HttpStatus.CONFLICT);

        verify(langRepository).create(prePersistLang);
    }

    /**
     * Test the happy path and generate snippets for lang get
     */
    @Test
    public void getShouldReturnOneEntryById() throws Exception {
        String inputLang = updateLang.getLang();

        given(langRepository.get(inputLang)).willReturn(Optional.of(updateLang));

        assert20xWithEntry(get(inputLang), HttpStatus.OK, updateLang)
                .andDo(document("get",
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
    public void getShouldReturnNotFound() throws Exception {
        String inputLang = prePersistLang.getLang();
        String expectedMessage = messages.getMessage("error.entity.notFound", inputLang);

        given(langRepository.get(inputLang)).willReturn(Optional.empty());

        assertError(get(inputLang), HttpStatus.NOT_FOUND)
                .andExpect(jsonPath("$.detail", equalTo(expectedMessage)));

        verify(langRepository).get(inputLang);
    }

    /**
     * Test the happy path and generate snippets for lang delete
     */
    @Test
    public void deleteShouldReturnSuccessResponse() throws Exception {
        String inputLang = persistedLang.getLang();

        doNothing().when(langRepository).delete(inputLang);

        performRequest(delete(inputLang))
            .andExpect(status().is(HttpStatus.OK.value()))
            .andExpect(content().string(""))
            .andDo(document("delete",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    pathParameters(
                            parameterWithName("lang").description("The language")
                    )
            ));

        verify(langRepository).delete(inputLang);
    }

    @Test
    public void deleteShouldFailIfNotFound() throws Exception {
        String inputLang = prePersistLang.getLang();

        doThrow(new DataRetrievalFailureException("Some error text")).when(langRepository).delete(inputLang);

        assertError(delete(inputLang), HttpStatus.BAD_REQUEST);

        verify(langRepository).delete(inputLang);
    }

    @Test
    public void updateShouldReturnValidationErrorWithEmptyContent() throws Exception {
        String inputContent = "{}";
        String inputLang = "ENG";

        assertValidationError(put(inputLang, inputContent), requiredLangField);
    }

    @Test
    public void updateShouldFailWithDifferentPathAndEntityId() throws Exception {
        String inputLang = "DEU";
        String inputContent = prePersistLangJson;

        String expectedMessage = messages.getMessage("error.url.parameter.mismatch", inputLang, prePersistLang.getLang());

        assertError(put(inputLang, inputContent), HttpStatus.BAD_REQUEST)
                .andExpect(jsonPath("$.detail", equalTo(expectedMessage)));
    }

    @Test
    public void updateShouldFailIfNotFound() throws Exception {
        String inputLang = preUpdateLang.getLang();
        String inputContent = objectMapper.writeValueAsString(preUpdateLang);

        given(langRepository.update(preUpdateLang)).willThrow(new DataRetrievalFailureException("Some error text"));

        assertError(put(inputLang, inputContent), HttpStatus.BAD_REQUEST);

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

        assert20xWithEntry(put(inputLang, inputContent), HttpStatus.OK, updateLang)
                .andDo(document("put",
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

    private ResultActions assert20xWithEntry(RequestBuilder request, HttpStatus status, Lang expectedResult) throws Exception {
        return assertSuccess(request, status)
                .andExpect(jsonPath("$.lang", equalTo(expectedResult.getLang())))
                .andExpect(jsonPath("$.created", equalTo(getAsString(expectedResult.getCreated()))))
                .andExpect(jsonPath("$.createdBy", equalTo(expectedResult.getCreatedBy())));
    }

    private String getAsString(ZonedDateTime dateTime) throws JsonProcessingException {
        return objectMapper.writeValueAsString(dateTime).replaceAll("\"", "");
    }

}
