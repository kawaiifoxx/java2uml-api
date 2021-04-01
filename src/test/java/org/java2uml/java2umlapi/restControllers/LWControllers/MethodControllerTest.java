package org.java2uml.java2umlapi.restControllers.LWControllers;

import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.io.FileDeleteStrategy;
import org.java2uml.java2umlapi.fileStorage.repository.ProjectInfoRepository;
import org.java2uml.java2umlapi.lightWeight.ClassOrInterface;
import org.java2uml.java2umlapi.lightWeight.EnumLW;
import org.java2uml.java2umlapi.lightWeight.Method;
import org.java2uml.java2umlapi.lightWeight.repository.*;
import org.java2uml.java2umlapi.restControllers.exceptions.LightWeightNotFoundException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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
@DisplayName("When using MethodController,")
@DirtiesContext
class MethodControllerTest {
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
    EnumLWRepository enumLWRepository;
    @Autowired
    ClassRelationRepository classRelationRepository;

    private ClassOrInterface classOrInterface;
    private EnumLW enumLW;
    private List<Method> classMethodList;
    private List<Method> enumMethodList;

    @BeforeEach
    void setUp() throws Exception {
        var source = getSource(mvc, sourceRepository, TEST_FILE_1);
        classOrInterface = classOrInterfaceRepository.findAllByParent(source)
                .stream()
                .filter(classOrInterface1 -> !methodRepository.findAllByParent(classOrInterface1).isEmpty())
                .findAny().orElseThrow(() -> new RuntimeException("Cannot get classOrInterface with methods"));
        enumLW = enumLWRepository.findAllByParent(source)
                .stream()
                .filter(enumLW1 -> !methodRepository.findAllByParent(enumLW1).isEmpty())
                .findAny().orElseThrow(() -> new RuntimeException("Cannot get enumLW with methods"));
        classMethodList = methodRepository.findAllByParent(classOrInterface);
        enumMethodList = methodRepository.findAllByParent(enumLW);
    }

    @Test
    @DisplayName("on valid request to one, response should be valid and should have status code 200 OK")
    void one() {
        var methodList = classMethodList;
        methodList.addAll(enumMethodList);
        methodList.forEach(method -> {
            var uri = "/api/method/" + method.getId();
            try {
                mvc.perform(get(uri))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id", is(method.getId().intValue())))
                        .andExpect(jsonPath("$.name", is(method.getName())))
                        .andExpect(jsonPath("$.signature", is(method.getSignature())))
                        .andExpect(jsonPath("$.visibility", is(method.getVisibility())))
                        .andExpect(jsonPath("$.static", is(method.isStatic())))
                        .andExpect(jsonPath("$._links.self.href", containsString(uri)))
                        .andExpect(jsonPath("$._links.body.href", containsString("/body/")))
                        .andExpect(jsonPath("$._links.callGraph.href", containsString("/call-graph/")))
                        .andExpect(jsonPath("$._links.methods.href", containsString("/by-parent/")));

            } catch (Exception exception) {
                throw new RuntimeException("Unable to perform get on" + uri, exception);
            }
        });
    }

    @Test
    @DisplayName("on valid request to allByParent, response should be valid and should have status code 200 OK")
    void allByClassOrInterface() throws Exception {
        Object parsedResponse = performGetOn(classOrInterface.getId(), "/class-or-interface");
        assertThatMethodNamesMatchesTheActualMethodNames(parsedResponse, classMethodList);
    }

    @Test
    @DisplayName("on valid request to allByParent, response should be valid and should have status code 200 OK")
    void allByEnum() throws Exception {
        Object parsedResponse = performGetOn(enumLW.getId(), "/enum/");
        assertThatMethodNamesMatchesTheActualMethodNames(parsedResponse, enumMethodList);
    }

    @Test
    @DisplayName("given that method does not exist sending get request to one(), should yield a response with 404.")
    void whenMethodDoesNotExistSendingGetToOne_shouldYieldResponseWith404NotFound() throws Exception {
        var method = classMethodList.get(0);
        removeMethodFromClassOrInterface(method);

        assertThatOnPerformingGetProvidedExceptionIsThrown(
                mvc, "/api/method/" + method.getId(),
                LightWeightNotFoundException.class
        ).andExpect(status().isNotFound());
    }

    /**
     * Removes the provided method from the classOrInterface.
     *
     * @param method to be removed.
     */
    private void removeMethodFromClassOrInterface(Method method) {
        classOrInterface.setClassOrInterfaceMethods(classMethodList);
        classOrInterface.getClassOrInterfaceMethods().remove(method);
        classOrInterfaceRepository.save(classOrInterface);
    }

    @Test
    @DisplayName("given that parent does not exist sending get request to allByParent()," +
            " should yield a response with 404.")
    @Transactional
    void whenParentDoesNotExistSendingGetToAllByParent_shouldYieldResponseWith404NotFound() throws Exception {
        classRelationRepository.deleteAllByFrom(classOrInterface);
        classRelationRepository.deleteAllByTo(classOrInterface);
        classOrInterfaceRepository.delete(classOrInterface);

        assertThatOnPerformingGetProvidedExceptionIsThrown(
                mvc, "/api/method/by-parent/" + classOrInterface.getId(),
                LightWeightNotFoundException.class
        ).andExpect(status().isNotFound());
    }

    /**
     * Asserts that all the {@link Method} names match up with the names of provided {@link Method}s.
     *
     * @param parsedJson Json, from this json all the names will be extracted.
     * @param methodList {@link List} of {@link Method}
     */
    private void assertThatMethodNamesMatchesTheActualMethodNames(Object parsedJson, List<Method> methodList) {
        List<String> methodNames = JsonPath.read(parsedJson, "$._embedded.methodList[*].name");
        assertThat(new HashSet<>(methodNames))
                .isEqualTo(methodList.stream().map(Method::getName).collect(Collectors.toSet()));
    }

    /**
     * Performs a get request on "/api/method/by-parent/{parentId}"
     *
     * @param parentId   id of parent
     * @param parentLink to test.
     * @return Parsed Json
     */
    private Object performGetOn(Long parentId, String parentLink) throws Exception {
        String uri = "/api/method/by-parent/" + parentId;
        return parseJson(
                mvc.perform(get(uri))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$._links.self.href", containsString(uri)))
                        .andExpect(jsonPath("$._links.parent.href", containsString(parentLink)))
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