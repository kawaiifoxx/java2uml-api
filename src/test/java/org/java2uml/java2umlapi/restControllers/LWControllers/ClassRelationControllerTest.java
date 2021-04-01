package org.java2uml.java2umlapi.restControllers.LWControllers;

import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.io.FileDeleteStrategy;
import org.java2uml.java2umlapi.fileStorage.entity.ProjectInfo;
import org.java2uml.java2umlapi.fileStorage.repository.ProjectInfoRepository;
import org.java2uml.java2umlapi.lightWeight.ClassRelation;
import org.java2uml.java2umlapi.lightWeight.Source;
import org.java2uml.java2umlapi.lightWeight.repository.ClassOrInterfaceRepository;
import org.java2uml.java2umlapi.lightWeight.repository.ClassRelationRepository;
import org.java2uml.java2umlapi.lightWeight.repository.SourceRepository;
import org.java2uml.java2umlapi.parsedComponent.service.SourceComponentService;
import org.java2uml.java2umlapi.restControllers.exceptions.ClassRelationNotFoundException;
import org.java2uml.java2umlapi.restControllers.exceptions.LightWeightNotFoundException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
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
@DisplayName("When using ClassRelationController,")
@DirtiesContext
class ClassRelationControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ProjectInfoRepository projectInfoRepository;
    @Autowired
    SourceRepository sourceRepository;
    @Autowired
    ClassOrInterfaceRepository classOrInterfaceRepository;
    @Autowired
    ClassRelationRepository classRelationRepository;
    @Autowired
    SourceComponentService sourceComponentService;

    private ProjectInfo projectInfo;
    private Source source;
    private List<ClassRelation> classRelationList;
    private String classRelationURI;

    @BeforeEach
    void setUp() throws Exception {
        var parsedProjectInfoJson = parseJson(getMultipartResponse(doMultipartRequest(mvc, TEST_FILE_4)));
        projectInfo = getEntityFromJson(parsedProjectInfoJson, projectInfoRepository);
        var parsedSourceJson = parseJson(
                mvc.perform(get("" + JsonPath.read(parsedProjectInfoJson, "$._links.projectModel.href")))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString()
        );
        source = getEntityFromJson(parsedSourceJson, sourceRepository);
        projectInfo.setSource(source);
        classRelationList = classRelationRepository.findAllByParent(source);
        assertThat(classRelationList).isNotEmpty();
        classRelationURI = JsonPath.read(parsedSourceJson, "$._links.relations.href");
    }

    @Test
    @DisplayName("on valid request to one, response should be valid and should have status code 200 OK")
    void one() {
        classRelationList.forEach(classRelation -> {
            try {
                var uri = "/api/relation/" + classRelation.getId();
                mvc.perform(get(uri))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$._links.self.href", containsString(uri)))
                        .andExpect(jsonPath("$.id", is(classRelation.getId().intValue())))
                        .andExpect(jsonPath("$.relationsSymbol",
                                containsString(classRelation.getRelationsSymbol().getName())))
                        .andExpect(jsonPath("$._links.from.href",
                                containsString("/class-or-interface/")))
                        .andExpect(jsonPath("$._links.to.href",
                                containsString("/class-or-interface/")))
                        .andExpect(jsonPath("$._links.relations.href",
                                containsString("/by-source/")));
            } catch (Exception exception) {
                throw new RuntimeException("Unable to perform get on /api/relation/" + classRelation.getId());
            }
        });
    }

    @Test
    @DisplayName("on valid request to allBySource, response should be valid and should have status code 200 OK")
    void allBySource() throws Exception {
        var parsedResponse = parseJson(
                mvc.perform(get(classRelationURI))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$._links.self.href", containsString("/relation")))
                        .andExpect(jsonPath("$._links.parent.href", containsString("/source")))
                        .andReturn().getResponse().getContentAsString());
        List<Integer> classRelationIdList = JsonPath.read(parsedResponse, "$._embedded.classRelationList[*].id");
        assertThat(new HashSet<>(classRelationIdList))
                .isEqualTo(classRelationList.stream().map(ClassRelation::getId).map(Long::intValue)
                        .collect(Collectors.toSet()));
    }

    @Test
    @DisplayName("given that source is not present sending get request to allBySource should result in 404 Not found.")
    void whenSourceIsNotPresent_thenShouldResponseShouldBe404NotFound() throws Exception {
        projectInfoRepository.delete(projectInfo);

        assertThatOnPerformingGetProvidedExceptionIsThrown(
                mvc, classRelationURI, LightWeightNotFoundException.class
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("given that class relation is not present sending get request to one should result in 404 Not found.")
    void whenClassRelationIsNotPresent_thenShouldResponseShouldBe404NotFound() throws Exception {
        var classRelation = classRelationList.get(0);
        removeClassRelation(classRelation);

        assertThatOnPerformingGetProvidedExceptionIsThrown(
                mvc, "/api/relation/" + classRelation.getId(), ClassRelationNotFoundException.class
        ).andExpect(status().isNotFound());
    }

    private void removeClassRelation(ClassRelation classRelation) {
        source.setClassRelationList(classRelationList);
        source.getClassRelationList().remove(classRelation);
        sourceRepository.save(source);
    }

    @AfterAll
    public static void tearDown() throws IOException {
        //Release all resources first.
        JarTypeSolver.ResourceRegistry.getRegistry().cleanUp();
        //Then delete directory.
        FileDeleteStrategy.FORCE.delete(TMP_DIR.toFile());
    }
}