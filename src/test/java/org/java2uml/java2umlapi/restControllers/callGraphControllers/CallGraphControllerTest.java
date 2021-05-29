package org.java2uml.java2umlapi.restControllers.callGraphControllers;

import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.io.FileDeleteStrategy;
import org.java2uml.java2umlapi.fileStorage.entity.ProjectInfo;
import org.java2uml.java2umlapi.fileStorage.repository.ProjectInfoRepository;
import org.java2uml.java2umlapi.lightWeight.Method;
import org.java2uml.java2umlapi.lightWeight.Source;
import org.java2uml.java2umlapi.lightWeight.repository.ClassOrInterfaceRepository;
import org.java2uml.java2umlapi.lightWeight.repository.MethodRepository;
import org.java2uml.java2umlapi.lightWeight.repository.SourceRepository;
import org.java2uml.java2umlapi.lightWeight.service.MethodSignatureToMethodIdMapService;
import org.java2uml.java2umlapi.parsedComponent.service.SourceComponentService;
import org.java2uml.java2umlapi.restControllers.exceptions.LightWeightNotFoundException;
import org.java2uml.java2umlapi.restControllers.exceptions.MethodNameToMethodIdNotFoundException;
import org.java2uml.java2umlapi.restControllers.exceptions.ParsedComponentNotFoundException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.java2uml.java2umlapi.restControllers.ControllerTestUtils.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Tag("WebApiTest")
@DisplayName("When using CallGraphController,")
@DirtiesContext
class CallGraphControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ProjectInfoRepository projectInfoRepository;
    @Autowired
    SourceRepository sourceRepository;
    @Autowired
    ClassOrInterfaceRepository classOrInterfaceRepository;
    @Autowired
    MethodRepository methodRepository;
    @Autowired
    MethodSignatureToMethodIdMapService methodIdMapService;
    @Autowired
    SourceComponentService sourceComponentService;

    private static final String CALL_GRAPH_URI = "/api/call-graph/";
    private Method method;
    private Source source;
    private ProjectInfo projectInfo;

    @BeforeEach
    void setUp() throws Exception {
        var parsedJson = parseJson(getMultipartResponse(doMultipartRequest(mvc, TEST_FILE_4)));
        String sourceUri = JsonPath.read(parsedJson, "$._links.projectModel.href");
        //Generate Source.
        mvc.perform(get(sourceUri))
                .andDo(print())
                .andExpect(status().isOk());
        this.projectInfo = getEntityFromJson(parsedJson, projectInfoRepository);
        this.source = projectInfo.getSource();
        this.method = getMethod();
    }

    @Test
    @DisplayName("given that request is valid, response code should be 200 " +
            "and response should contain CallGraphRelation.")
    void getCallGraph() throws Exception {
        mvc.perform(get(CALL_GRAPH_URI + method.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href",
                        containsString(CALL_GRAPH_URI + method.getId())));
    }

    @Test
    @DisplayName("when method name to method id map is not present then should get 500 internal server error")
    void whenMethodNameToMethodIdIsNotPresent_thenReturn500InternalServerError() throws Exception {
        methodIdMapService.delete(source.getId());
        Exception e = getException(status().isInternalServerError());
        assertThat(e).isInstanceOf(MethodNameToMethodIdNotFoundException.class);
    }

    @Test
    @DisplayName("when source component is not present then should get 500 internal server error")
    void whenSourceComponentIsNotPresent_thenReturn500InternalServerError() throws Exception {
        sourceComponentService.delete(projectInfo.getId());
        Exception e = getException(status().isInternalServerError());
        assertThat(e).isInstanceOf(ParsedComponentNotFoundException.class);
    }

    @Test
    @DisplayName("when source is not present then should get 404 not found")
    void whenSourceIsNotPresent_thenReturn400NotFound() throws Exception {
        // This delete cascades to Source
        projectInfoRepository.delete(projectInfo);
        Exception e = getException(status().isNotFound());
        assertThat(e).isInstanceOf(LightWeightNotFoundException.class);
    }

    /**
     * Performs given test and before returning exception checks whether it is null or not.
     *
     * @param matcher test you want to perform.
     * @return {@link Exception} thrown by method being tested.
     */
    private Exception getException(ResultMatcher matcher) throws Exception {
        Exception e = mvc.perform(get(CALL_GRAPH_URI + method.getId()))
                .andDo(print())
                .andExpect(matcher)
                .andReturn().getResolvedException();
        assertThat(e).isNotNull();
        return e;
    }

    /**
     * @return any  method instance.
     */
    private Method getMethod() {
        return classOrInterfaceRepository.findAllByParent(source)
                .stream()
                .flatMap(classOrInterface -> methodRepository.findAllByParent(classOrInterface).stream())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No method found."));
    }

    @AfterAll
    public static void tearDown() throws IOException {
        //Release all resources first.
        JarTypeSolver.ResourceRegistry.getRegistry().cleanUp();
        //Then delete directory.
        FileDeleteStrategy.FORCE.delete(TMP_DIR.toFile());
    }
}