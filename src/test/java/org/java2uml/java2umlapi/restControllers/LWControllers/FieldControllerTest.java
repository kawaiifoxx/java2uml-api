package org.java2uml.java2umlapi.restControllers.LWControllers;

import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import org.apache.commons.io.FileDeleteStrategy;
import org.java2uml.java2umlapi.lightWeight.ClassOrInterface;
import org.java2uml.java2umlapi.lightWeight.EnumLW;
import org.java2uml.java2umlapi.lightWeight.Field;
import org.java2uml.java2umlapi.lightWeight.repository.ClassOrInterfaceRepository;
import org.java2uml.java2umlapi.lightWeight.repository.EnumLWRepository;
import org.java2uml.java2umlapi.lightWeight.repository.FieldRepository;
import org.java2uml.java2umlapi.lightWeight.repository.SourceRepository;
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
@DisplayName("When using FieldController,")
@DirtiesContext
class FieldControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    SourceRepository sourceRepository;
    @Autowired
    ClassOrInterfaceRepository classOrInterfaceRepository;
    @Autowired
    FieldRepository fieldRepository;
    @Autowired
    EnumLWRepository enumLWRepository;

    private ClassOrInterface classOrInterface;
    private EnumLW enumLW;
    private List<Field> classFieldList;
    private List<Field> enumFieldList;


    @BeforeEach
    void setUp() throws Exception {
        var source = getSource(mvc, sourceRepository, TEST_FILE_1);
        classOrInterface = classOrInterfaceRepository.findAllByParent(source)
                .stream()
                .filter(classOrInterface1 -> !fieldRepository.findAllByParent(classOrInterface1).isEmpty())
                .findAny().orElseThrow(() -> new RuntimeException("Unable to get class or interface with fields."));
        enumLW = enumLWRepository.findAllByParent(source)
                .stream()
                .filter(enumLW1 -> !fieldRepository.findAllByParent(enumLW1).isEmpty())
                .findAny().orElseThrow(() -> new RuntimeException("Unable to find an enum with fields."));
        classFieldList = fieldRepository.findAllByParent(classOrInterface);
        enumFieldList = fieldRepository.findAllByParent(enumLW);
    }

    @Test
    @DisplayName("on valid request to one, response should be valid and should have status code 200 OK")
    void one() {
        var fieldList = classFieldList;
        fieldList.addAll(enumFieldList);

        fieldList.forEach(
                field -> {
                    var uri = "/api/field/" + field.getId();
                    try {
                        mvc.perform(get(uri))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id", is(field.getId().intValue())))
                                .andExpect(jsonPath("$.name", is(field.getName())))
                                .andExpect(jsonPath("$.visibility", is(field.getVisibility())))
                                .andExpect(jsonPath("$.typeName", is(field.getTypeName())))
                                .andExpect(jsonPath("$.static", is(field.isStatic())))
                                .andExpect(jsonPath("$._links.self.href", containsString(uri)))
                                .andExpect(jsonPath("$._links.fields.href", containsString("/by-parent")));
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
        );
    }

    @Test
    @DisplayName("on valid request to allByParent, response should be valid and should have status code 200 OK")
    void allByClassOrInterface() throws Exception {
        var parsedJson = performGetOn(
                mvc, "/api/field/by-parent/" + classOrInterface.getId(), "/class-or-interface");
        assertThatAllNamesMatch(parsedJson, "$._embedded.fieldList[*].name", classFieldList);
    }

    @Test
    @DisplayName("on valid request to allByParent, response should be valid and should have status code 200 OK")
    void allByEnumLw() throws Exception {
        var parsedJson = performGetOn(
                mvc, "/api/field/by-parent/" + enumLW.getId(), "/enum");
        assertThatAllNamesMatch(parsedJson, "$._embedded.fieldList[*].name", enumFieldList);
    }

    @Test
    @DisplayName("given that parent is not present on performing get on allByParent should get 404 not found.")
    @Transactional
    void whenParentIsNotPresentOnPerformingGetOnAllByParent_shouldResultInA404NotFound() throws Exception {
        enumLWRepository.delete(enumLW);
        assertThatOnPerformingGetProvidedExceptionIsThrown(
                mvc, "/api/field/by-parent/" + enumLW.getId(), LightWeightNotFoundException.class
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("given that field is not present on performing get on one() should get 404 not found.")
    @Transactional
    void whenFieldIsNotPresentOnPerformingGetOnOne_shouldResultInA404NotFound() throws Exception {
        var field = enumFieldList.get(0);
        fieldRepository.delete(field);
        assertThatOnPerformingGetProvidedExceptionIsThrown(
                mvc, "/api/field/" + field.getId(), LightWeightNotFoundException.class
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