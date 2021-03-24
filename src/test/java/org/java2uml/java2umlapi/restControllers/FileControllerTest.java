package org.java2uml.java2umlapi.restControllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.FileInputStream;
import java.nio.file.Path;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Tag("WebApiTest")
@DisplayName("When using FileController,")
class FileControllerTest {
    private static final String URI = "/api/files";
    private static final Path TEST_FILE_1 = Path.of("src/test/testSources/WebApiTest/FileControllerTest/test1.zip");
    private static final String CONTENT_TYPE = "application/zip";

    @Autowired
    MockMvc mvc;

    @Test
    @DisplayName("given that request is valid, upload should return http status 201 and entity model of project info")
    void upload() throws Exception {
        var multiPartFile = new MockMultipartFile(
                "file", "test1.zip", CONTENT_TYPE, new FileInputStream(TEST_FILE_1.toFile())
        );

        mvc.perform(multipart(URI).file(multiPartFile))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$._links.self.href", containsString("/api/project-info/")))
                .andExpect(jsonPath("$._links.umlSvg.href", containsString("/api/uml/svg/")))
                .andExpect(jsonPath("$._links.umlText.href", containsString("/api/uml/plant-uml-code/")));
    }

    //TODO: Add more tests.
}