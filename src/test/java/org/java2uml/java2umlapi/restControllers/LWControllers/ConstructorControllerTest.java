package org.java2uml.java2umlapi.restControllers.LWControllers;

import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.io.FileDeleteStrategy;
import org.java2uml.java2umlapi.fileStorage.repository.ProjectInfoRepository;
import org.java2uml.java2umlapi.lightWeight.ClassOrInterface;
import org.java2uml.java2umlapi.lightWeight.Constructor;
import org.java2uml.java2umlapi.lightWeight.EnumLW;
import org.java2uml.java2umlapi.lightWeight.Source;
import org.java2uml.java2umlapi.lightWeight.repository.*;
import org.java2uml.java2umlapi.parsedComponent.service.SourceComponentService;
import org.java2uml.java2umlapi.restControllers.exceptions.LightWeightNotFoundException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
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
@DisplayName("When using ConstructorController,")
@DirtiesContext
class ConstructorControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ProjectInfoRepository projectInfoRepository;
    @Autowired
    SourceRepository sourceRepository;
    @Autowired
    ClassOrInterfaceRepository classOrInterfaceRepository;
    @Autowired
    ConstructorRepository constructorRepository;
    @Autowired
    EnumLWRepository enumLWRepository;
    @Autowired
    ClassRelationRepository classRelationRepository;
    @Autowired
    SourceComponentService sourceComponentService;

    private ClassOrInterface classOrInterface;
    private EnumLW enumLW;
    private List<Constructor> classConstructorList;
    private List<Constructor> enumConstructorList;

    @BeforeEach
    void setUp() throws Exception {
        Source source = getSource(mvc, sourceRepository, TEST_FILE_1);
        classOrInterface = classOrInterfaceRepository.findAllByParent(source).stream()
                .filter(classOrInterface1 -> !constructorRepository.findConstructorByParent(classOrInterface1).isEmpty())
                .findFirst().orElseThrow(() -> new RuntimeException("Unable to get classOrInterface with constructors."));
        classConstructorList = constructorRepository.findConstructorByParent(classOrInterface);
        enumLW = enumLWRepository.findAllByParent(source).stream()
                .filter(enumLW1 -> !constructorRepository.findConstructorByParent(enumLW1).isEmpty())
                .findFirst().orElseThrow(() -> new RuntimeException("Unable to get EnumLW with constructors."));
        enumConstructorList = constructorRepository.findConstructorByParent(enumLW);
    }

    @Test
    @DisplayName("on valid request to one, response should be valid and should have status code 200 OK")
    void one() {
        var constructorList = new ArrayList<>(classConstructorList);
        constructorList.addAll(enumConstructorList);
        constructorList.forEach(
                constructor -> {
                    var uri = "/api/constructor/" + constructor.getId();
                    try {
                        mvc.perform(get(uri))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id", is(constructor.getId().intValue())))
                                .andExpect(jsonPath("$.name", is(constructor.getName())))
                                .andExpect(jsonPath("$.visibility", is(constructor.getVisibility())))
                                .andExpect(jsonPath("$.compilerGenerated", is(constructor.isCompilerGenerated())))
                                .andExpect(jsonPath("$._links.body.href", containsString("/body")))
                                .andExpect(jsonPath("$._links.self.href", containsString(uri)));
                    } catch (Exception exception) {
                        throw new RuntimeException("Unable to perform get on " + uri);
                    }
                }
        );
    }

    @Test
    @DisplayName("on valid request to allByParent with classOrInterface as parent," +
            " response should be valid and should have status code 200 OK")
    void allByClassOrInterface() throws Exception {
        Object parsedResponse = performGetOn(classOrInterface.getId(), "/class-or-interface");
        assertThatAllConstructorNamesMatch(parsedResponse, classConstructorList);
    }

    @Test
    @DisplayName("on valid request to allByParent with EnumLW as parent," +
            " response should be valid and should have status code 200 OK")
    void allByEnumLW() throws Exception {
        Object parsedResponse = performGetOn(enumLW.getId(), "/enum");
        assertThatAllConstructorNamesMatch(parsedResponse, enumConstructorList);
    }

    @Test
    @DisplayName("given that constructor is not found get request to one()," +
            "should result in a 404 and a LightWeightNotFoundException.")
    void whenConstructorCannotBeFound_callToOneShouldReturn404NotFound() throws Exception {
        var constructor = classConstructorList.get(0);
        removeConstructorFromClassOrInterface(constructor);
        var e = mvc.perform(get("/api/constructor/" + constructor.getId()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn().getResolvedException();
        assertThat(e).isNotNull().isInstanceOf(LightWeightNotFoundException.class);
    }

    @Test
    @DisplayName("given that parent of constructor is not found get request to allByParent," +
            "should result in a 404 and a LightWeightNotFoundException.")
    @Transactional
    void whenParentOfConstructorCannotBeFound_allByParentShouldReturn404NotFound() throws Exception {
        classRelationRepository.deleteAllByFrom(classOrInterface);
        classRelationRepository.deleteAllByTo(classOrInterface);
        classOrInterfaceRepository.delete(classOrInterface);
        var e = mvc.perform(get("/api/constructor/by-parent/" + classOrInterface.getId()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn().getResolvedException();
        assertThat(e).isNotNull().isInstanceOf(LightWeightNotFoundException.class);
    }

    /**
     * Removes provided constructor from classOrInterface.
     *
     * @param constructor to be removed.
     */
    private void removeConstructorFromClassOrInterface(Constructor constructor) {
        classOrInterface.setClassConstructors(classConstructorList);
        classOrInterface.getClassConstructors().remove(constructor);
        classOrInterfaceRepository.save(classOrInterface);
    }

    /**
     * Asserts that all the constructor names match up with the names of provided constructors.
     *
     * @param parsedJson           Json, from this json all the names will be extracted.
     * @param classConstructorList {@link List} of {@link Constructor}
     */
    private void assertThatAllConstructorNamesMatch(Object parsedJson, List<Constructor> classConstructorList) {
        List<String> constructorNames = JsonPath.read(parsedJson, "$._embedded.constructorList[*].name");
        assertThat(new HashSet<>(constructorNames))
                .isEqualTo(classConstructorList.stream().map(Constructor::getName).collect(Collectors.toSet()));
    }

    /**
     * Performs a get request on "/api/constructor/by-parent/{parentId}"
     *
     * @param parentId   id of parent
     * @param parentLink to test.
     * @return Parsed Json
     */
    private Object performGetOn(Long parentId, String parentLink) throws Exception {
        var uri = "/api/constructor/by-parent/" + parentId;
        return parseJson(
                mvc.perform(get(uri))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$._links.self.href", containsString(uri)))
                        .andExpect(jsonPath("$._links.parent.href",
                                containsString(parentLink)))
                        .andReturn().getResponse().getContentAsString()
        );
    }

    @AfterAll
    public static void tearDown() throws IOException {
        //Release all resources first.
        JarTypeSolver.ResourceRegistry.getRegistry().cleanUp();
        //Then delete directory.
        FileDeleteStrategy.FORCE.delete(TMP_DIR.toFile());
    }
}