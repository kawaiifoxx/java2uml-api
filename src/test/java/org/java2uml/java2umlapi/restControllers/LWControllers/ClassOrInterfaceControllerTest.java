package org.java2uml.java2umlapi.restControllers.LWControllers;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import org.java2uml.java2umlapi.fileStorage.entity.ProjectInfo;
import org.java2uml.java2umlapi.fileStorage.repository.ProjectInfoRepository;
import org.java2uml.java2umlapi.lightWeight.ClassOrInterface;
import org.java2uml.java2umlapi.lightWeight.Source;
import org.java2uml.java2umlapi.lightWeight.repository.ClassOrInterfaceRepository;
import org.java2uml.java2umlapi.lightWeight.repository.SourceRepository;
import org.java2uml.java2umlapi.parsedComponent.service.SourceComponentService;
import org.java2uml.java2umlapi.restControllers.exceptions.LightWeightNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.java2uml.java2umlapi.restControllers.ControllerTestUtils.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Tag("WebApiTest")
@DisplayName("When using ClassOrInterfaceController,")
@DirtiesContext
class ClassOrInterfaceControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ProjectInfoRepository projectInfoRepository;
    @Autowired
    SourceRepository sourceRepository;
    @Autowired
    ClassOrInterfaceRepository classOrInterfaceRepository;
    @Autowired
    SourceComponentService sourceComponentService;

    private ProjectInfo projectInfo;
    private Source source;
    private List<ClassOrInterface> classOrInterfaceList;
    private String classesURI;

    @BeforeEach
    void setUp() throws Exception {
        var parsedJsonForProjectInfo = Configuration.defaultConfiguration().jsonProvider()
                .parse(getMultipartResponse(doMultipartRequest(mvc, TEST_FILE_4)));
        projectInfo = getProjectInfo(parsedJsonForProjectInfo, projectInfoRepository);
        String sourceURI = JsonPath.read(parsedJsonForProjectInfo, "$._links.projectModel.href");
        var sourceUnparsed = mvc.perform(get(sourceURI))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var parsedSource = Configuration.defaultConfiguration().jsonProvider().parse(sourceUnparsed);
        classesURI = JsonPath.read(parsedSource, "$._links.classes.href");
        //noinspection OptionalGetWithoutIsPresent
        projectInfo = projectInfoRepository.findById(projectInfo.getId()).get();
        source = projectInfo.getSource();
        classOrInterfaceList = classOrInterfaceRepository.findAllByParent(source);
    }

    @Test
    @DisplayName("on valid request, response should be valid and should have status code 200 OK")
    void one() {
        classOrInterfaceList.forEach(classOrInterface -> {
            try {
                mvc.perform(get("/api/class-or-interface/" + classOrInterface.getId())).andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$._links.constructors.href",
                                containsString("/constructor/by-parent")))
                        .andExpect(jsonPath("$._links.fields.href",
                                containsString("/field/by-parent")))
                        .andExpect(jsonPath("$._links.methods.href",
                                containsString("/method/by-parent")))
                        .andExpect(jsonPath("$._links.body.href",
                                containsString("/body/by-parent")))
                        .andExpect(jsonPath("$._links.self.href",
                                containsString("/class-or-interface/")))
                        .andExpect(jsonPath("$._links.parent.href",
                                containsString("/source/")))
                        .andExpect(jsonPath("$._links.classes.href",
                                containsString("/class-or-interface/by-source")))
                        .andExpect(jsonPath("$._links.relations.href",
                                containsString("/relation/by-source")));
            } catch (Exception e) {
                //something went wrong.
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    @DisplayName("given that classOrInterface with given id is not present, response code should be not found with 404")
    void whenClassOrInterfaceIsNotPresent_thenResponseCodeShouldBe404() throws Exception {
        var classOrInterface = classOrInterfaceList.get(0);
        projectInfoRepository.delete(projectInfo);
        var e = mvc.perform(get("/api/class-or-interface/" + classOrInterface.getId())).andDo(print())
                .andExpect(status().isNotFound())
                .andReturn().getResolvedException();
        assertThat(e).isNotNull().isInstanceOf(LightWeightNotFoundException.class);
    }

    @Test
    void whenSourceIsNotPresent_thenResponseCodeShouldBe404() throws Exception {
        projectInfoRepository.delete(projectInfo);
        var e = mvc.perform(get("/api/class-or-interface/by-source/" + source.getId()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn().getResolvedException();
        assertThat(e).isNotNull().isInstanceOf(LightWeightNotFoundException.class);
    }

    @Test
    @DisplayName("on valid request, response should be valid and should have status code 200 OK")
    void allBySource() throws Exception {
        var unparsedResponse = mvc.perform(get(classesURI))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href", is(classesURI)))
                .andExpect(jsonPath("$._links.parent.href", containsString("/source")))
                .andReturn().getResponse().getContentAsString();
        var parsedResponse = Configuration.defaultConfiguration().jsonProvider().parse(unparsedResponse);
        List<String> classNames = JsonPath.read(parsedResponse, "$._embedded.classOrInterfaceList[*].name");
        assertThat(new HashSet<>(classNames))
                .isEqualTo(classOrInterfaceList.stream().map(ClassOrInterface::getName).collect(Collectors.toSet()));
    }
}