package org.java2uml.java2umlapi.restControllers.LWControllers;

import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import org.apache.commons.io.FileDeleteStrategy;
import org.java2uml.java2umlapi.lightWeight.EnumConstant;
import org.java2uml.java2umlapi.lightWeight.EnumLW;
import org.java2uml.java2umlapi.lightWeight.repository.EnumConstantRepository;
import org.java2uml.java2umlapi.lightWeight.repository.EnumLWRepository;
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
class EnumConstantControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    SourceRepository sourceRepository;
    @Autowired
    EnumLWRepository enumLWRepository;
    @Autowired
    EnumConstantRepository enumConstantRepository;

    private EnumLW enumLW;
    private List<EnumConstant> enumConstants;

    @BeforeEach
    void setUp() throws Exception {
        var source = getSource(mvc, sourceRepository, TEST_FILE_1);
        enumLW = enumLWRepository.findAllByParent(source)
                .stream().filter(enumLW1 -> !enumConstantRepository.findAllByParent(enumLW1).isEmpty())
                .findAny().orElseThrow(() -> new RuntimeException("No enum present with enum constants."));
        enumConstants = enumConstantRepository.findAllByParent(enumLW);
    }

    @Test
    @DisplayName("on valid request to one, response should be valid and should have status code 200 OK")
    void one() {
        enumConstants.forEach(
                enumConstant -> {
                    var uri = "/api/enum-constant/" + enumConstant.getId();
                    try {
                        mvc.perform(get(uri))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id", is(enumConstant.getId().intValue())))
                                .andExpect(jsonPath("$.name", is(enumConstant.getName())))
                                .andExpect(jsonPath("$._links.self.href", containsString(uri)))
                                .andExpect(jsonPath("$._links.enumConstants.href", containsString("/by-parent")))
                                .andExpect(jsonPath("$._links.parent.href", containsString("/enum")));
                    } catch (Exception exception) {
                        throw new RuntimeException("Unable to perform get request on " + uri, exception);
                    }
                }
        );
    }

    @Test
    @DisplayName("on valid request to allByParent, response should be valid and should have status code 200 OK")
    void allByParent() throws Exception {
        var parsedJson = performGetOn(mvc, "/api/enum-constant/by-parent/" + enumLW.getId(), "/enum/");
        assertThatAllNamesMatch(parsedJson, "$._embedded.enumConstantList[*].name", enumConstants);
    }

    @Test
    @DisplayName("given that parent is not present on sending a get request to allByParent," +
            " should result in 404 not found.")
    @Transactional
    void whenParentIsNotPresentThenSendingGetOnAllByParent_shouldResultIn404NotFound() throws Exception {
        enumLWRepository.delete(enumLW);
        assertThatOnPerformingGetProvidedExceptionIsThrown(
                mvc, "/api/enum-constant/by-parent/" + enumLW.getId(), LightWeightNotFoundException.class
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("given that enum constant is not present on sending a get request to one(), should result in 404 not found.")
    @Transactional
    void whenEnumConstantIsNotPresentThenSendingGetOnOne_shouldResultIn404NotFound() throws Exception {
        var enumConstant = enumConstants.get(0);
        enumConstantRepository.delete(enumConstant);

        assertThatOnPerformingGetProvidedExceptionIsThrown(
                mvc, "/api/enum-constant/" + enumConstant.getId(), LightWeightNotFoundException.class
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