package org.java2uml.java2umlapi.restControllers;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import org.java2uml.java2umlapi.fileStorage.repository.ProjectInfoRepository;
import org.java2uml.java2umlapi.fileStorage.service.UnzippedFileStorageService;
import org.java2uml.java2umlapi.parsedComponent.service.SourceComponentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.java2uml.java2umlapi.restControllers.ControllerTestUtils.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Tag("WebApiTest")
@DisplayName("When using UMLController,")
class UMLControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    UnzippedFileStorageService fileStorageService;
    @Autowired
    ProjectInfoRepository repository;
    @Autowired
    SourceComponentService sourceComponentService;

    @Test
    @DisplayName("given that request is valid then response should be UML code with response 200 OK.")
    void getPUMLCode() throws Exception {
        var parsedJson = Configuration.defaultConfiguration().jsonProvider()
                .parse(getMultipartResponse(doMultipartRequest(mvc, TEST_FILE_4)));
        String requestURI = JsonPath.read(parsedJson, "$._links.umlText.href");
        String svgURI = JsonPath.read(parsedJson, "$._links.umlSvg.href");
        String projectInfoURI = JsonPath.read(parsedJson, "$._links.self.href");
        var response = mvc.perform(get(requestURI))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href", is(requestURI)))
                .andExpect(jsonPath("$._links.umlSvg.href", is(svgURI)))
                .andExpect(jsonPath("$._links.projectInfo.href", is(projectInfoURI)))
                .andReturn().getResponse().getContentAsString();

        String uml = JsonPath.read(response, "$.content");

        assertThat(uml).describedAs("UML should start with @startuml and end with @enduml")
                .startsWith("@startuml").endsWith("@enduml");
    }

    @Test
    @DisplayName("given that request is valid then response should be UML svg with response 200 OK.")
    void getSvg() throws Exception {
        var parsedJson = Configuration.defaultConfiguration().jsonProvider()
                .parse(getMultipartResponse(doMultipartRequest(mvc, TEST_FILE_4)));
        String requestURI = JsonPath.read(parsedJson, "$._links.umlSvg.href");
        var response = mvc.perform(get(requestURI))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.CONTENT_DISPOSITION))
                .andExpect(content().contentType("image/svg+xml"))
                .andReturn().getResponse().getContentAsString();

        assertThat(response).describedAs("should contain uml").contains("@startuml").contains("@enduml");
    }
}