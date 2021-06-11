package org.java2uml.java2umlapi.restControllers;

import com.jayway.jsonpath.JsonPath;
import org.java2uml.java2umlapi.fileStorage.repository.ProjectInfoRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.io.FileInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.java2uml.java2umlapi.restControllers.ControllerTestUtils.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Tag("WebApiTest")
@DisplayName("When using FileController,")
@DirtiesContext
class FileControllerTest {
    private static final String URI = "/api/files";

    @Autowired
    MockMvc mvc;
    @Autowired
    ProjectInfoRepository projectInfoRepository;

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
            " upload should return http status 415")
    void uploadWithInvalidFile() throws Exception {
        mvc.perform(
                multipart(URI).file(
                        new MockMultipartFile(
                                "file",
                                "invalidFile.txt",
                                "text/plain",
                                "This file is an invalid file.".getBytes())
                )
        ).andExpect(status().isUnsupportedMediaType());
    }

    @SuppressWarnings({"OptionalGetWithoutIsPresent", "BusyWait"})
    @Test
    @DisplayName("when zip does not contain .java files, upload should return http status 400 bad request.")
    void uploadWithZipWithoutJavaFile() throws Exception {
        var parsedJson = parseJson(getMultipartResponse(doMultipartRequest(mvc, TEST_FILE_2)));
        var projectInfo =
                getEntityFromJson(parsedJson, projectInfoRepository);
        while (true) {
            var jsonObject = parseJson(getMultipartResponse(
                    mvc.perform(get((String) JsonPath.read(parsedJson, "$._links.self.href")))));
            boolean shouldBreak = (Boolean) JsonPath.read(jsonObject, "$.parsed") ||
                    (Boolean) JsonPath.read(jsonObject, "$.badRequest");
            if (shouldBreak) break;
            Thread.sleep(500);
        }
        projectInfo = projectInfoRepository.findById(projectInfo.getId()).get();
        assertThat(projectInfo.isBadRequest()).isTrue();
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
    public static void tearDown() throws IOException, InterruptedException {
        cleanUp();
    }
}