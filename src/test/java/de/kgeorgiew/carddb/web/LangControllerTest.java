package de.kgeorgiew.carddb.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.kgeorgiew.carddb.ConstrainedFields;
import de.kgeorgiew.carddb.domain.Lang;
import de.kgeorgiew.carddb.service.LangRepository;
import de.kgeorgiew.carddb.service.MessagesService;
import de.kgeorgiew.carddb.service.time.FixedTimeService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.hypermedia.LinksSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static de.kgeorgiew.carddb.HalRestAsserts.*;
import static de.kgeorgiew.carddb.RestRequestUtil.addJsonContent;
import static de.kgeorgiew.carddb.RestRequestUtil.addAcceptJson;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
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

    private final String expectedErrorField = "lang";

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
    }

//    @Test
//    public void shouldReturnGermanErrorMessage() throws Exception {
//        Locale expectedLocal = Locale.GERMAN;
//        String content = "";
//        String expectedMsg = messageSource.getMessage("error.request.body.invalid",
//                null, expectedLocal);
//
//        MockHttpServletRequestBuilder request = addAcceptJson(HttpMethod.POST)
//                .locale(expectedLocal)
//                .content(content);
//
//
//        assertJsonErrorContent(request, HttpStatus.BAD_REQUEST);
//    }

    @Test
    public void createShouldReturn400OnValidationError() throws Exception {
        String inputContent = "{}";

        ResultActions actualResponse = mvc.perform(addJsonContent(post(baseUrl), inputContent));

        assertJsonValidationError(actualResponse, expectedErrorField);
    }

    /**
     * Test the happy path and generate snippets for lang post
     *
     */
    @Test
    public void createShouldReturnJsonWithAllFieldsSet() throws Exception {
        Lang lang = TestLangs.withoutCreatedUpdated("eng");
        Lang langAfterSave = TestLangs.withCreated("eng");
        String content = objectMapper.writeValueAsString(lang);

        given(langRepository.create(lang)).willReturn(langAfterSave);

        ResultActions actualResponse = mvc.perform(addJsonContent(post(baseUrl), content));
        HttpStatus expectedStatus = HttpStatus.CREATED;

        assertJsonResponseType(actualResponse, expectedStatus);
        assertExpectedContent(actualResponse, langAfterSave);

        doDocs(actualResponse,
                "post",
                interactionLinks,
                requestFields(
                        new ConstrainedFields(Lang.class).withPath("lang").description("The language")
                ),
                createResponseFields
        );

        verify(langRepository).create(lang);
    }

    @Test
    public void createShouldReturn409OnDuplicateEntry() throws Exception {
        Lang lang = TestLangs.withoutCreatedUpdated("eng");
        String content = objectMapper.writeValueAsString(lang);

        String expectedMsg = "Entry already exists";
        given(langRepository.create(lang)).willThrow(new DuplicateKeyException(expectedMsg));

        ResultActions actualResponse = mvc.perform(
                addJsonContent(post(baseUrl), content)
        );
        HttpStatus expectedStatus = HttpStatus.CONFLICT;

        assertJsonErrorContent(actualResponse, expectedStatus, expectedMsg);

        verify(langRepository).create(lang);
    }

    /**
     * Test the happy path and generate snippets for lang get
     */
    @Test
    public void getShouldReturnOneEntryById() throws Exception {
        Lang updateLang = TestLangs.withUpdated("fra");
        String urlPathLang = updateLang.getLang();

        given(langRepository.get(urlPathLang)).willReturn(Optional.of(updateLang));

        ResultActions actualResponse = mvc.perform(addAcceptJson(get(urlWithId, urlPathLang)));
        HttpStatus expectedStatus = HttpStatus.OK;

        assertJsonResponseType(actualResponse, expectedStatus);
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

        verify(langRepository).get(urlPathLang);
    }

    @Test
    public void getShouldReturn404OnUnknownId() throws Exception {
        String urlPathLang = "fra";
        String expectedMessage = messages.getMessage("error.entity.notFound", urlPathLang);

        given(langRepository.get(urlPathLang)).willReturn(Optional.empty());

        ResultActions actualResponse = mvc.perform(addAcceptJson(get(urlWithId, urlPathLang)));
        HttpStatus expectedStatus = HttpStatus.NOT_FOUND;

        assertJsonErrorContent(actualResponse, expectedStatus, expectedMessage);

        verify(langRepository).get(urlPathLang);
    }

    /**
     * Test the happy path and generate snippets for lang delete
     */
    @Test
    public void deleteShouldReturnEmpty200OnSuccess() throws Exception {
        String urlPathLang = "eng";

        doNothing().when(langRepository).delete(urlPathLang);

        ResultActions actualResponse = mvc.perform(addAcceptJson(delete(urlWithId, urlPathLang)));
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

        verify(langRepository).delete(urlPathLang);
    }

//    @Test
//    public void deleteShouldReturn404IfNotFound() throws Exception {
//        String inputLang = prePersistLang.getLang();
//
//        doThrow(new DataRetrievalFailureException("Some error text")).when(langRepository).delete(inputLang);
//
//        ResultActions actualResponse = mvc.perform(addAcceptJson(delete(urlWithId, inputLang)));
//        HttpStatus expectedStatus = HttpStatus.NOT_FOUND;
//
//        assertJsonErrorContent(actualResponse, expectedStatus);
//
//        verify(langRepository).delete(inputLang);
//    }

    @Test
    public void updateShouldReturnValidationError() throws Exception {
        String inputContent = "{}";
        String inputLang = "eng";

        ResultActions actualResponse = mvc.perform(addJsonContent(put(urlWithId, inputLang), inputContent));

        assertJsonValidationError(actualResponse, expectedErrorField);
    }

    @Test
    public void updateShouldReturn400WithDifferentPathAndEntityId() throws Exception {
        String urlPathLang = "deu";
        Lang createdLang = TestLangs.withCreated("eng");
        String content = objectMapper.writeValueAsString(createdLang);

        ResultActions actualResponse = mvc.perform(addJsonContent(put(urlWithId, urlPathLang), content));
        HttpStatus expectedStatus = HttpStatus.BAD_REQUEST;
        String expectedMessage = messages.getMessage("error.url.parameter.mismatch", urlPathLang, createdLang.getLang());

        assertJsonErrorContent(actualResponse, expectedStatus, expectedMessage);
    }

//    @Test
//    public void updateShouldReturn404IfNotFound() throws Exception {
//        String inputLang = preUpdateLang.getLang();
//        String inputContent = objectMapper.writeValueAsString(preUpdateLang);
//
//        given(langRepository.update(preUpdateLang)).willThrow(new DataRetrievalFailureException("Some error text"));
//
//        ResultActions actualResponse = mvc.perform(addAcceptJson(put(urlWithId, inputLang), inputContent));
//        HttpStatus expectedStatus = HttpStatus.BAD_REQUEST;
//
//        assertJsonErrorContent(actualResponse, expectedStatus);
//
//        verify(langRepository).update(preUpdateLang);
//    }

    /**
     * Test the happy path and generate snippets for lang put
     */
    @Test
    public void updateShouldSuccess() throws Exception {
        Lang createdLang = TestLangs.withCreated("eng");
        Lang updatedLang = TestLangs.withUpdated("fra");
        String urlPathLang = createdLang.getLang();
        String content = objectMapper.writeValueAsString(createdLang);

        given(langRepository.update(createdLang)).willReturn(updatedLang);

        ResultActions actualResponse = mvc.perform(
                addJsonContent(put(urlWithId, urlPathLang), content)
        );
        HttpStatus expectedStatus = HttpStatus.OK;

        assertJsonResponseType(actualResponse, expectedStatus);
        assertExpectedContent(actualResponse, updatedLang);

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

        verify(langRepository).update(createdLang);
    }

    @Test
    public void getListShouldReturn5SortedEntries() throws Exception {
        int page = 0;
        int size = 5;
        List<Lang> langs = TestLangs.random(size);
        PageRequest pageable = new PageRequest(page, size, new Sort(Sort.Direction.ASC, "lang"));
        PageImpl<Lang> dataPage = new PageImpl<>(langs, pageable, 10);

        given(langRepository.list(anyObject())).willReturn(dataPage);

        ResultActions actualResponse = mvc.perform(
                addAcceptJson(get(baseUrl)
                        .param("page", "" + pageable.getPageNumber())
                        .param("size", "" + pageable.getPageSize())
                        .param("sort", "" + pageable.getSort().toString())
                )
        );
        HttpStatus expectedStatus = HttpStatus.OK;

        assertJsonResponseType(actualResponse, expectedStatus);

        //TODO Check expected order
//        langs.sort(Comparator.comparing(Lang::getLang));

        actualResponse.andExpect(jsonPath("$._embedded").isNotEmpty())
            .andExpect(jsonPath("$._embedded.ex:items.[*]", hasSize(5)))
            //TODO Check expected order
//            .andExpect(jsonPath("$._embedded.ex:items.[*].lang", Matchers.contains("")))
            .andExpect(jsonPath("$._embedded..lang").isNotEmpty())
            .andExpect(jsonPath("$._embedded.*.[*]._links").isArray())
            .andExpect(jsonPath("$.page.size", equalTo(size)))
            .andExpect(jsonPath("$.page.totalElements", equalTo(10)))
            .andExpect(jsonPath("$.page.totalPages", equalTo(2)))
            .andExpect(jsonPath("$.page.number", equalTo(page)))
            .andExpect(jsonPath("$._links.next").exists())
            .andExpect(jsonPath("$._links.first").exists())
            .andExpect(jsonPath("$._links.last").exists())
            .andExpect(jsonPath("$._links.self").exists());

        verify(langRepository, times(1)).list(anyObject());
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
