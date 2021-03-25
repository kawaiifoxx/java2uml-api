package org.java2uml.java2umlapi.restControllers;

import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import org.apache.commons.io.FileDeleteStrategy;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Tag("WebApiTest")
@DisplayName("When using FileController,")
class FileControllerTest {
    private static final Path TMP_DIR = Paths.get("tmp");
    private static final String URI = "/api/files";
    private static final Path TEST_FILE_1 = Path.of("src/test/testSources/WebApiTest/FileControllerTest/test1.zip");
    private static final Path TEST_FILE_2 = Path.of("src/test/testSources/WebApiTest/FileControllerTest/" +
            "non_java_file_zip_test.zip");
    private static final Path TEST_FILE_3 = Path.of(
            "src/test/testSources/WebApiTest/FileControllerTest/java_code_with_syntax_errors.zip");
    private static final Path TEST_FILE_4 = Path.of(
            "src/test/testSources/WebApiTest/FileControllerTest/code_without_included_dependencies.zip"
    );
    private static final String CONTENT_TYPE = "application/zip";

    @Autowired
    MockMvc mvc;

    @Test
    @DisplayName("given that request is valid, upload should return http status 201 and entity model of project info")
    void uploadWithValidRequest() throws Exception {
        var multiPartFile = new MockMultipartFile(
                "file", "test1.zip", CONTENT_TYPE, new FileInputStream(TEST_FILE_1.toFile())
        );

        mvc.perform(multipart(URI).file(multiPartFile))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.projectName", is("test1.zip")))
                .andExpect(jsonPath("$.fileType", is(CONTENT_TYPE)))
                .andExpect(jsonPath("$.size", is((int) multiPartFile.getSize())))
                .andExpect(jsonPath("$._links.self.href", containsString("/api/project-info/")))
                .andExpect(jsonPath("$._links.umlSvg.href", containsString("/api/uml/svg/")))
                .andExpect(jsonPath("$._links.umlText.href", containsString("/api/uml/plant-uml-code/")))
                .andExpect(jsonPath("$._links.projectModel.href",
                        containsString("/api/source/by-project-info/")));
    }

    @Test
    @DisplayName("when file type is not application/zip," +
            " upload should return http status 400")
    void uploadWithInvalidFile() throws Exception {
        mvc.perform(
                multipart(URI).file(
                        new MockMultipartFile(
                                "file",
                                "invalidFile.txt",
                                "text/plain",
                                "This file is an invalid file.".getBytes())
                )
        ).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("when zip does not contain .java files, upload should return http status 400 bad request.")
    void uploadWithZipWithoutJavaFile() throws Exception {
        var multipart = new MockMultipartFile("file", "non_java_file_zip_test.zip", CONTENT_TYPE,
                new FileInputStream(TEST_FILE_2.toFile()));

        mvc.perform(multipart(URI).file(multipart))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("when zip contains .java files with syntax errors," +
            " upload should return http status 201 created.")
    void uploadWithZipContainingJavaFilesWithErrors() throws Exception {
        var multipart = new MockMultipartFile("file", "java_code_with_syntax_errors.zip", CONTENT_TYPE,
                new FileInputStream(TEST_FILE_3.toFile()));

        mvc.perform(multipart(URI).file(multipart))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("when zip contains .java files without included dependencies," +
            " upload should return http status 201 created.")
    void uploadWithZipContainingJavaFilesWithoutIncludedDependencies() throws Exception {
        var multipart = new MockMultipartFile("file", "code_without_included_dependencies.zip",
                CONTENT_TYPE, new FileInputStream(TEST_FILE_4.toFile()));

        mvc.perform(multipart(URI).file(multipart))
                .andExpect(status().isCreated());
    }

    @AfterAll
    public static void tearDown() throws IOException {
        //Release all resources first.
        JarTypeSolver.ResourceRegistry.getRegistry().cleanUp();
        //Then delete directory.
        FileDeleteStrategy.FORCE.delete(TMP_DIR.toFile());
    }
}