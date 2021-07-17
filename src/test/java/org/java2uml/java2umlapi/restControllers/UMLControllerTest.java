package org.java2uml.java2umlapi.restControllers;

import com.jayway.jsonpath.JsonPath;
import org.java2uml.java2umlapi.fileStorage.entity.ProjectInfo;
import org.java2uml.java2umlapi.fileStorage.repository.ProjectInfoRepository;
import org.java2uml.java2umlapi.fileStorage.service.UnzippedFileStorageService;
import org.java2uml.java2umlapi.parsedComponent.SourceComponent;
import org.java2uml.java2umlapi.parsedComponent.service.SourceComponentService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

import static com.jayway.jsonpath.JsonPath.read;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.java2uml.java2umlapi.restControllers.ControllerTestUtils.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Tag("WebApiTest")
@DisplayName("When using UMLController,")
@DirtiesContext
class UMLControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    UnzippedFileStorageService fileStorageService;
    @Autowired
    ProjectInfoRepository projectInfoRepository;
    @Autowired
    SourceComponentService sourceComponentService;

    @Test
    @DisplayName("given that request is valid then response should be UML code with response 200 OK.")
    void getPUMLCode() throws Exception {
        var parsedJson = parseJson(getMultipartResponse(doMultipartRequest(mvc, TEST_FILE_4)));
        String requestURI = read(parsedJson, "$._links.umlText.href");
        String svgURI = read(parsedJson, "$._links.umlSvg.href");
        String projectInfoURI = read(parsedJson, "$._links.self.href");
        waitTillSourceComponentGetsGenerated(sourceComponentService, getIdFromSelfLink(parsedJson));
        waitTillResourceGetsGenerated(mvc, requestURI);
        var response = mvc.perform(get(requestURI))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href", is(requestURI)))
                .andExpect(jsonPath("$._links.umlSvg.href", is(svgURI)))
                .andExpect(jsonPath("$._links.projectInfo.href", is(projectInfoURI)))
                .andReturn().getResponse().getContentAsString();

        String uml = read(response, "$.content");

        assertThat(uml).describedAs("UML should start with @startuml and end with @enduml")
                .startsWith("@startuml").endsWith("@enduml");
    }

    @Test
    @DisplayName("given that project was not uploaded sending request to" +
            " \"/api/plant-uml-code/{projectInfoId}\" should give 404 not found.")
    void whenProjectIsNotUploaded_thenResponseShouldBe404NotFound() throws Exception {
        mvc.perform(get("/api/uml/plant-uml-code/" + Long.MAX_VALUE))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0]", containsString("ProjectInfo not found")));
    }

    @Test
    @DisplayName("given that SourceComponent is yet to be generated " +
            "sending request to \"/api/plant-uml-code/{projectInfoId}\" should give 202 internal server error.")
    @DirtiesContext
    void whenSourceComponentIsNotPresent_thenShouldGet202_From_getPUMLCode() throws Exception {
        assertThatSourceComponentIsNotPresentOn("$._links.umlText.href");
    }

    @Test
    @DisplayName("given that request is valid then response should be UML svg with response 200 OK.")
    void getSvg() throws Exception {
        var parsedJson = parseJson(getMultipartResponse(doMultipartRequest(mvc, TEST_FILE_4)));
        String requestURI = read(parsedJson, "$._links.umlSvg.href");
        waitTillSourceComponentGetsGenerated(sourceComponentService, getIdFromSelfLink(parsedJson));
        waitTillResourceGetsGenerated(mvc, requestURI);
        var response = mvc.perform(get(requestURI))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.CONTENT_DISPOSITION))
                .andExpect(content().contentType("image/svg+xml"))
                .andReturn().getResponse().getContentAsString();

        assertThat(response).describedAs("should contain uml").contains("@startuml").contains("@enduml");
    }

    @Test
    @DisplayName("given that project was not uploaded sending request to" +
            " \"/api/svg/{projectInfoId}\" should give 404 not found.")
    void whenProjectIsNotUploaded_thenResponseOfGetSVGShouldBe404NotFound() throws Exception {
        mvc.perform(get("/api/uml/svg/" + Long.MAX_VALUE))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0]", containsString("ProjectInfo not found")));
    }

    @Test
    @DisplayName("given that SourceComponent is yet to be generated, sending request to " +
            " \"/api/svg/{projectInfoId}\" should give 202.")
    @DirtiesContext
    void whenSourceComponentIsNotPresent_thenShouldGet202_From_getSvg() throws Exception {
        assertThatSourceComponentIsNotPresentOn("$._links.umlSvg.href");
    }

    /**
     * Deletes {@link SourceComponent} and then perform tests that right exception is thrown.
     *
     * @param s query for {@link JsonPath}
     */
    private void assertThatSourceComponentIsNotPresentOn(String s) throws Exception {
        var parsedJson = parseJson(getMultipartResponse(doMultipartRequest(mvc, TEST_FILE_4)));
        String requestURI = JsonPath.read(parsedJson, s);

        ProjectInfo projectInfo = getEntityFromJson(parsedJson, projectInfoRepository);
        sourceComponentService.delete(projectInfo.getId());

        var e = mvc.perform(get(requestURI))
                .andDo(print())
                .andExpect(status().is(202))
                .andReturn().getResolvedException();

        assertThat(e).isNotNull().isInstanceOf(ResponseStatusException.class);
    }

    @AfterAll
    public static void tearDown() throws IOException, InterruptedException {
        cleanUp();
    }
}