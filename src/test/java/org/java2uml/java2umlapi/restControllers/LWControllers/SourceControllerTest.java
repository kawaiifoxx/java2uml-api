package org.java2uml.java2umlapi.restControllers.LWControllers;

import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.io.FileDeleteStrategy;
import org.java2uml.java2umlapi.fileStorage.entity.ProjectInfo;
import org.java2uml.java2umlapi.fileStorage.repository.ProjectInfoRepository;
import org.java2uml.java2umlapi.lightWeight.Source;
import org.java2uml.java2umlapi.lightWeight.repository.SourceRepository;
import org.java2uml.java2umlapi.parsedComponent.service.SourceComponentService;
import org.java2uml.java2umlapi.restControllers.exceptions.LightWeightNotFoundException;
import org.java2uml.java2umlapi.restControllers.exceptions.ParsedComponentNotFoundException;
import org.java2uml.java2umlapi.restControllers.exceptions.ProjectInfoNotFoundException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.java2uml.java2umlapi.restControllers.ControllerTestUtils.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Tag("WebApiTest")
@DisplayName("When using SourceController,")
class SourceControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ProjectInfoRepository projectInfoRepository;
    @Autowired
    SourceRepository sourceRepository;
    @Autowired
    SourceComponentService sourceComponentService;

    private String sourceURIByProjectInfo;
    private ProjectInfo projectInfo;
    private Source source;

    @BeforeEach
    void setUp() throws Exception {
        var parsedJson = Configuration.defaultConfiguration().jsonProvider()
                .parse(getMultipartResponse(doMultipartRequest(mvc, TEST_FILE_4)));
        projectInfo = getProjectInfo(parsedJson, projectInfoRepository);
        sourceURIByProjectInfo = JsonPath.read(parsedJson, "$._links.projectModel.href");
    }

    /**
     * Performs a get request on {@code "/api/source/by-project-info/{projectInfoId}"}
     *
     * @return {@link ResultActions} to perform more checks.
     */
    private ResultActions performGetRequestOnSourceByProjectInfoId() throws Exception {
        return mvc.perform(get(sourceURIByProjectInfo))
                .andDo(print());
    }

    /**
     * Sets the {@link Source} field if null.
     *
     * @throws RuntimeException if source cannot be fetched.
     */
    private void setSource() {
        if (source == null) {
            projectInfo = projectInfoRepository.findById(projectInfo.getId()).orElseThrow(
                    () -> new RuntimeException("Unable to get source.")
            );
            source = projectInfo.getSource();
        }
    }

    @Test
    @DisplayName("on sending valid request, should receive valid response with 200 OK")
    void one() throws Exception {
        performGetRequestOnSourceByProjectInfoId().andExpect(status().isOk());
        setSource();
        mvc.perform(get("/api/source/" + source.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href",
                        containsString("/api/source/" + source.getId())))
                .andExpect(jsonPath("$._links.projectInfo.href", containsString("/project-info/")))
                .andExpect(jsonPath("$._links.classes.href",
                        containsString("class-or-interface/by-source/")))
                .andExpect(jsonPath("$._links.enums.href", containsString("enum/by-source/")))
                .andExpect(jsonPath("$._links.relations.href", containsString("relation/by-source/")));
    }

    @Test
    @DisplayName("on sending valid request with projectInfo id, should receive valid response with 200 OK")
    void findByProjectId() throws Exception {
        performGetRequestOnSourceByProjectInfoId().andExpect(status().isOk());
    }

    @Test
    @DisplayName("given that project info is not present, response should be 404 not found.")
    void whenProjectInfoIsNotPresent_thenShouldRespondWith404NotFound() throws Exception {
        projectInfoRepository.delete(projectInfo);
        var e = performGetRequestOnSourceByProjectInfoId()
                .andExpect(status().isNotFound()).andReturn().getResolvedException();
        assertThat(e).isNotNull();
        assertThatThrownBy(() -> {
            throw e;
        }).isInstanceOf(ProjectInfoNotFoundException.class);
    }

    @Test
    @DisplayName("given that source component is not present, respond with 500 internal server error.")
    void whenSourceComponentIsNotPresent_thenShouldRespondWith500InternalServerError() throws Exception {
        sourceComponentService.delete(projectInfo.getSourceComponentId());
        var e = performGetRequestOnSourceByProjectInfoId()
                .andExpect(status().isInternalServerError()).andReturn().getResolvedException();
        assertThat(e).isNotNull();
        assertThatThrownBy(() -> {
            throw e;
        }).isInstanceOf(ParsedComponentNotFoundException.class);
    }

    @Test
    @DisplayName("given that source has not been generated, response code should be 404 Not found.")
    void whenSourceIsNotGenerated_thenShouldRespondWith404NotFoundError() throws Exception {
        var e = mvc.perform(get("/api/source/" + Long.MAX_VALUE))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn().getResolvedException();

        assertThat(e).isNotNull();
        assertThatThrownBy(() -> {
            throw e;
        }).isInstanceOf(LightWeightNotFoundException.class);
    }

    @AfterAll
    public static void tearDown() throws IOException {
        //Release all resources first.
        JarTypeSolver.ResourceRegistry.getRegistry().cleanUp();
        //Then delete directory.
        FileDeleteStrategy.FORCE.delete(TMP_DIR.toFile());
    }
}