package org.java2uml.java2umlapi.restControllers;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * <p>
 * An helper class for providing setup methods for controller tests.
 * </p>
 *
 * @author kawaiifox
 */
public abstract class ControllerTestUtils {
    public static final Path TMP_DIR = Paths.get("tmp");
    public static final String URI = "/api/files";
    public static final Path TEST_FILE_1 = Path.of("src/test/testSources/WebApiTest/FileControllerTest/test1.zip");
    public static final Path TEST_FILE_2 = Path.of("src/test/testSources/WebApiTest/FileControllerTest/" +
            "non_java_file_zip_test.zip");
    public static final Path TEST_FILE_3 = Path.of(
            "src/test/testSources/WebApiTest/FileControllerTest/java_code_with_syntax_errors.zip");
    public static final Path TEST_FILE_4 = Path.of(
            "src/test/testSources/WebApiTest/FileControllerTest/code_without_included_dependencies.zip"
    );
    public static final String CONTENT_TYPE = "application/zip";

    public static ResultActions doMultipartRequest(MockMvc mvc, Path path) throws Exception {
        var multiPartFile = new MockMultipartFile("file",
                path.getFileName().toString(),
                CONTENT_TYPE,
                new FileInputStream(path.toFile()));

        return mvc.perform(multipart(URI).file(multiPartFile))
                .andDo(print());
    }
}
