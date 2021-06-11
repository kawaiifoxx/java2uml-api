package org.java2uml.java2umlapi.restControllers.LWControllers;

import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import org.apache.commons.io.FileDeleteStrategy;
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
import java.util.List;

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
        Object parsedResponse = performGetOn(
                mvc, "/api/method/by-parent/" + classOrInterface.getId(), "/class-or-interface");
        assertThatAllNamesMatch(parsedResponse, "$._embedded.methodList[*].name", classMethodList);
    }

    @Test
    @DisplayName("on valid request to allByParent, response should be valid and should have status code 200 OK")
    void allByEnum() throws Exception {
        Object parsedResponse = performGetOn(mvc, "/api/method/by-parent/" + enumLW.getId(), "/enum/");
        assertThatAllNamesMatch(parsedResponse, "$._embedded.methodList[*].name", enumMethodList);
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
    protected void removeMethodFromClassOrInterface(Method method) {
        classOrInterface.setClassOrInterfaceMethods(classMethodList);
        classOrInterface.getClassOrInterfaceMethods().remove(method);
        classOrInterfaceRepository.save(classOrInterface);
        method.setParent(null);
        methodRepository.delete(method);
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

    @AfterAll
    public static void tearDown() throws IOException {
        //Release all resources first.
        JarTypeSolver.ResourceRegistry.getRegistry().cleanUp();
        //Then delete directory.
        FileDeleteStrategy.FORCE.delete(TMP_DIR.toFile());
    }
}