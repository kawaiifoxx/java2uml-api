package org.java2uml.java2umlapi.restControllers.LWControllers;

import org.java2uml.java2umlapi.lightWeight.*;
import org.java2uml.java2umlapi.lightWeight.repository.*;
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
import org.springframework.transaction.annotation.Transactional;

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
class BodyControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    SourceRepository sourceRepository;
    @Autowired
    BodyRepository bodyRepository;
    @Autowired
    ClassOrInterfaceRepository classOrInterfaceRepository;
    @Autowired
    MethodRepository methodRepository;
    @Autowired
    ConstructorRepository constructorRepository;
    @Autowired
    EnumLWRepository enumLWRepository;

    private Source source;
    private ClassOrInterface classOrInterface;
    private EnumLW enumLW;
    private Method method;
    private Constructor constructor;

    private Body classBody;
    private Body enumBody;
    private Body methodBody;
    private Body constructorBody;

    @BeforeEach
    void setUp() throws Exception {
        source = getSource(mvc, sourceRepository, TEST_FILE_1);
        enumLW = enumLWRepository.findAllByParent(source).stream().findAny().orElseThrow(
                () -> new RuntimeException("Unable to get enum from enumLWRepository.")
        );
        classOrInterface = classOrInterfaceRepository.findAllByParent(source)
                .stream()
                .filter(classOrInterface1 -> !constructorRepository.findConstructorByParent(classOrInterface1).isEmpty())
                .filter(classOrInterface1 -> !methodRepository.findAllByParent(classOrInterface1).isEmpty())
                .findAny()
                .orElseThrow(() -> new RuntimeException("No class exist with constructor and method in test sources"));
        constructor = constructorRepository.findConstructorByParent(classOrInterface).stream().findAny()
                .orElseThrow(() -> new RuntimeException("No constructor exist in test sources."));
        method = methodRepository.findAllByParent(classOrInterface).stream().findAny()
                .orElseThrow(() -> new RuntimeException("No method exist in test sources."));
        classBody = bodyRepository.findByParent(classOrInterface)
                .orElseThrow(() -> new RuntimeException("Unable to get body for class."));
        constructorBody = bodyRepository.findByParent(constructor)
                .orElseThrow(() -> new RuntimeException("Unable to get body for constructor."));
        methodBody = bodyRepository.findByParent(method)
                .orElseThrow(() -> new RuntimeException("Unable to get body for method."));
        enumBody = bodyRepository.findByParent(enumLW)
                .orElseThrow(() -> new RuntimeException("Unable to get body for enum."));
    }

    @Test
    @DisplayName("on valid request to one, response should be valid and should have status code 200 OK")
    void one() throws Exception {
        performGetOn("/api/body/" + classBody.getId(), "/class-or-interface/", classBody);
        performGetOn("/api/body/" + enumBody.getId(), "/enum/", enumBody);
        performGetOn("/api/body/" + constructorBody.getId(), "/constructor/", constructorBody);
        performGetOn("/api/body/" + methodBody.getId(), "/method/", methodBody);
    }

    @Test
    @DisplayName("on valid request to bodyParentId, response should be valid and should have status code 200 OK")
    void bodyByParentId() throws Exception {
        performGetOn("/api/body/by-parent/" + classOrInterface.getId(), "/class-or-interface/", classBody);
        performGetOn("/api/body/by-parent/" + enumLW.getId(), "/enum/", enumBody);
        performGetOn("/api/body/by-parent/" + constructor.getId(), "/constructor/", constructorBody);
        performGetOn("/api/body/by-parent/" + method.getId(), "/method/", methodBody);
    }

    @Test
    @DisplayName("given that body is not present performing get on one(), should give 404 not found")
    @Transactional
    void whenBodyIsNotPresent_thenGetRequestToOneShouldThrow404NotFound() throws Exception {
        classOrInterface.setBody(null);
        bodyRepository.delete(classBody);

        assertThatOnPerformingGetProvidedExceptionIsThrown(
                mvc, "/api/body/" + classBody.getId(), LightWeightNotFoundException.class
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("given that parent of body is not present performing get on bodyByParentId(), should give 404 not found")
    @Transactional
    void whenParentOfBodyIsNotPresent_thenGetRequestToBodyByParentIdShouldThrow404NotFound() throws Exception {
        source.getEnumLWList().remove(enumLW);

        assertThatOnPerformingGetProvidedExceptionIsThrown(
                mvc, "/api/body/by-parent/" + enumLW.getId(), LightWeightNotFoundException.class
        ).andExpect(status().isNotFound());
    }

    /**
     * Perform get request and some checks.
     *
     * @param uri        on which get request will be sent.
     * @param parentLink link of parent class.
     * @param body       Body which will be checked against retrieved response.
     * @throws Exception if Unable to perform get request.
     */
    private void performGetOn(String uri, String parentLink, Body body) throws Exception {
        mvc.perform(get(uri))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href", containsString("/api/body/" + body.getId())))
                .andExpect(jsonPath("$._links.parent.href", containsString(parentLink)))
                .andExpect(jsonPath("$.id", is(body.getId().intValue())))
                .andExpect(jsonPath("$.content", is(body.getContent())));
    }
}