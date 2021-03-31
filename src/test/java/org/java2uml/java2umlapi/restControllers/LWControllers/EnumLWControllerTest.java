package org.java2uml.java2umlapi.restControllers.LWControllers;

import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.io.FileDeleteStrategy;
import org.java2uml.java2umlapi.fileStorage.entity.ProjectInfo;
import org.java2uml.java2umlapi.fileStorage.repository.ProjectInfoRepository;
import org.java2uml.java2umlapi.lightWeight.EnumLW;
import org.java2uml.java2umlapi.lightWeight.Source;
import org.java2uml.java2umlapi.lightWeight.repository.EnumLWRepository;
import org.java2uml.java2umlapi.lightWeight.repository.SourceRepository;
import org.java2uml.java2umlapi.parsedComponent.service.SourceComponentService;
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
@DisplayName("When using EnumLWController,")
@DirtiesContext
class EnumLWControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ProjectInfoRepository projectInfoRepository;
    @Autowired
    SourceRepository sourceRepository;
    @Autowired
    EnumLWRepository enumLWRepository;
    @Autowired
    SourceComponentService sourceComponentService;

    private ProjectInfo projectInfo;
    private Source source;
    private List<EnumLW> enumLWList;
    private String enumListURI;


    @BeforeEach
    void setUp() throws Exception {
        var parsedProjectInfoJson = parseJson(
                getMultipartResponse(doMultipartRequest(mvc, ENUM_LW_CONTROLLER_TEST_FILE)));
        projectInfo = getEntityFromJson(parsedProjectInfoJson, projectInfoRepository);
        var parsedSourceJson = parseJson(mvc.perform(
                get(JsonPath.read(parsedProjectInfoJson, "$._links.projectModel.href") + ""))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString());
        source = getEntityFromJson(parsedSourceJson, sourceRepository);
        projectInfo.setSource(source);
        enumLWList = enumLWRepository.findAllByParent(source);
        enumListURI = JsonPath.read(parsedSourceJson, "$._links.enums.href");
    }

    @Test
    @DisplayName("on valid request, response should be valid and should have status code 200 OK")
    void one() {
        String enumURI = "/api/enum/";
        enumLWList.forEach(enumLW -> {
            try {
                mvc.perform(get(enumURI + enumLW.getId()))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id", is(enumLW.getId().intValue())))
                        .andExpect(jsonPath("$.name", is(enumLW.getName())))
                        .andExpect(jsonPath("$.packageName", is(enumLW.getPackageName())))
                        .andExpect(jsonPath("$._links.enumConstants.href",
                                containsString("/enum-constant")))
                        .andExpect(jsonPath("$._links.fields.href",
                                containsString("/field")))
                        .andExpect(jsonPath("$._links.constructors.href",
                                containsString("/constructor")))
                        .andExpect(jsonPath("$._links.body.href",
                                containsString("/body")))
                        .andExpect(jsonPath("$._links.self.href",
                                containsString(enumURI + enumLW.getId())))
                        .andExpect(jsonPath("$._links.parent.href",
                                containsString("/source")));

            } catch (Exception exception) {
                throw new RuntimeException("Unable to send get request at /api/enum/" + enumLW.getId());
            }
        });
    }

    @Test
    @DisplayName("on valid request, response should be valid and should have status code 200 OK")
    void allBySource() throws Exception {
        var parsedEnumsJson = parseJson(
                mvc.perform(get(enumListURI))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$._links.self.href", containsString("/enum")))
                        .andReturn().getResponse().getContentAsString()
        );

        List<String> enumNames = JsonPath.read(parsedEnumsJson, "$._embedded.enumLWList[*].name");
        assertThat(new HashSet<>(enumNames))
                .isEqualTo(enumLWList.stream().map(EnumLW::getName).collect(Collectors.toSet()));
    }

    @Test
    @DisplayName("given that enumLW is not present on performing get on /api/enum/{enumID} should get 404 not found.")
    void whenEnumLWIsNotPresentOnPerformingGetRequest_ShouldGet404NotFound() throws Exception {
        var enumLW = enumLWList.get(0);
        removeEnumLW(enumLW);
        var e = mvc.perform(get("/api/enum/" + enumLW.getId()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn().getResolvedException();
        assertThat(e).isNotNull().isInstanceOf(LightWeightNotFoundException.class);
    }

    @Test
    @DisplayName("given that Source is not present on performing" +
            " get on /api/enum/by-source/{sourceID} should get 404 not found.")
    void whenSourceIsNotPresentOnPerformingGetRequest_ShouldGet404NotFound() throws Exception {
        projectInfoRepository.delete(projectInfo);
        var e = mvc.perform(get("/api/enum/by-source/" + source.getId()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn().getResolvedException();
        assertThat(e).isNotNull().isInstanceOf(LightWeightNotFoundException.class);
    }

    /**
     * Removes {@link EnumLW} from database.
     * @param enumLW to be removed.
     */
    private void removeEnumLW(EnumLW enumLW) {
        source.setEnumLWList(enumLWList);
        source.getEnumLWList().remove(enumLW);
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