package org.java2uml.java2umlapi.restControllers;

import org.java2uml.java2umlapi.fileStorage.entity.ProjectInfo;
import org.java2uml.java2umlapi.fileStorage.repository.ProjectInfoRepository;
import org.java2uml.java2umlapi.fileStorage.service.ClassDiagramSVGService;
import org.java2uml.java2umlapi.fileStorage.service.UMLCodeCacheService;
import org.java2uml.java2umlapi.parsedComponent.service.SourceComponentService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.java2uml.java2umlapi.restControllers.ControllerTestUtils.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Tag("WebApiTest")
@DisplayName("When using EventSubscriptionController, ")
@DirtiesContext
class EventSubscriptionControllerTest {
    private static final String URI = "/api/event";

    @Autowired
    ProjectInfoRepository projectInfoRepository;

    @Autowired
    MockMvc mvc;

    @Autowired
    SourceComponentService sourceComponentService;

    @Autowired
    UMLCodeCacheService umlCodeCacheService;

    @Autowired
    ClassDiagramSVGService classDiagramSVGService;

    ProjectInfo projectInfo;

    @BeforeEach
    void setUp() throws Exception {
        assertThat(projectInfoRepository).isNotNull();
        assertThat(sourceComponentService).isNotNull();
        assertThat(umlCodeCacheService).isNotNull();
        assertThat(classDiagramSVGService).isNotNull();

        generateProjectInfo(TEST_FILE_1);
    }

    @Test
    @DisplayName("while subscribing to parse event should get notifications for all parse events.")
    void subscribeToParseEvent() throws Exception {
        generateProjectInfo(JAVA2UML_API_SOURCE);

        //If already parsed then cannot subscribe to event.
        if (projectInfo.isParsed()) return;

        assertThatGeneratedEventContains(getMvcResult("/parse/"), "PARSE_SUCCEEDED");
    }

    @Test
    @DisplayName("while subscribing to uml code generation event should get notifications for all uml code generation events.")
    void subscribeToUMLCodeGenerationEvent() throws Exception {
        waitTillSourceComponentGetsGenerated(sourceComponentService, projectInfo.getId());
        var result = getMvcResult("/uml/code/");
        mvc.perform(get("/api/uml/plant-uml-code/" + projectInfo.getId())).andDo(print());

        assertThatGeneratedEventContains(result, "SUCCEEDED");
    }

    @Test
    @DisplayName("while subscribing to uml svg generation event should get notifications for all uml svg generation events.")
    void subscribeToUMLSVGGenerationEvent() throws Exception {
        waitTillSourceComponentGetsGenerated(sourceComponentService, projectInfo.getId());
        MvcResult result = getMvcResult("/uml/svg/");
        mvc.perform(get("/api/uml/svg/" + projectInfo.getId())).andDo(print());

        assertThatGeneratedEventContains(result, "SUCCEEDED");
    }

    @Test
    @DisplayName("while subscribing to source generation event should get notifications for all source generation events.")
    void subscribeToSourceGeneration() throws Exception {
        waitTillSourceComponentGetsGenerated(sourceComponentService, projectInfo.getId());
        MvcResult result = getMvcResult("/source/");
        mvc.perform(get("/api/source/by-project-info/" + projectInfo.getId())).andDo(print());

        assertThatGeneratedEventContains(result, "SUCCEEDED");
    }

    @Test
    @DisplayName("when subscribing to dependency matrix generation event, should get notifications for all dependency matrix generation events.")
    void subscribeToDependencyMatrixGeneration() throws Exception {
        waitTillSourceComponentGetsGenerated(sourceComponentService, projectInfo.getId());
        MvcResult result = getMvcResult("/dependency-matrix/");
        mvc.perform(get("/api/dependency-matrix/" + projectInfo.getId()));

        assertThatGeneratedEventContains(result, "SUCCEEDED");
    }

    /**
     * Generates project info.
     *
     * @param file {@link Path} of file to be tested.a
     */
    private void generateProjectInfo(Path file) throws Exception {
        projectInfo = getEntityFromJson(parseJson(
                        getMultipartResponse(
                                doMultipartRequest(mvc, file))),
                projectInfoRepository);
    }

    /**
     * Performs async dispatch.
     *
     * @param result On which dispatch is performed.
     */
    private void performAsyncDispatch(MvcResult result) throws Exception {
        mvc.perform(asyncDispatch(result))
                .andDo(log())
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/event-stream"));
    }

    /**
     * @param result From which event will be extracted.
     * @param text   Text to checked.
     */
    private void assertThatGeneratedEventContains(MvcResult result, String text) throws Exception {
        performAsyncDispatch(result);
        var event = result.getResponse().getContentAsString();
        assertThat(event).contains(text);
    }

    /**
     * Takes in uri and generates MvcResult.
     *
     * @param uri URI to which get request will be performed.
     * @return MvcResult
     */
    private MvcResult getMvcResult(String uri) throws Exception {
        return mvc.perform(get(URI + uri + projectInfo.getId()))
                .andExpect(request().asyncStarted())
                .andDo(log())
                .andReturn();
    }

    @AfterAll
    static void tearDown() throws IOException, InterruptedException {
        cleanUp();
    }
}