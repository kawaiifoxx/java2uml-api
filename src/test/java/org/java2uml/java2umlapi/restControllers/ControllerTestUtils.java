package org.java2uml.java2umlapi.restControllers;

import com.jayway.jsonpath.JsonPath;
import org.java2uml.java2umlapi.fileStorage.entity.ProjectInfo;
import org.java2uml.java2umlapi.fileStorage.repository.ProjectInfoRepository;
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

    /**
     * Performs a multipart request with ControllerTestUtils.URI.
     * @param mvc {@link MockMvc}
     * @param path path to file which you want to upload.
     * @return {@link ResultActions}
     */
    public static ResultActions doMultipartRequest(MockMvc mvc, Path path) throws Exception {
        var multiPartFile = new MockMultipartFile("file",
                path.getFileName().toString(),
                CONTENT_TYPE,
                new FileInputStream(path.toFile()));

        return mvc.perform(multipart(URI).file(multiPartFile))
                .andDo(print());
    }

    /**
     * @param resultActions {@link ResultActions} from which response will be extracted.
     * @return Response
     */
    public static String getMultipartResponse(ResultActions resultActions) throws Exception {
        return resultActions.andReturn()
                .getResponse()
                .getContentAsString();
    }

    /**
     * @param parsedJson takes in response in form of parsed Json.
     * @param repository {@link ProjectInfoRepository} from which {@link ProjectInfo} will be retrieved.
     * @return {@link ProjectInfo} from {@link ProjectInfoRepository}
     * @throws NumberFormatException if URI cannot be parsed for getting id.
     */
    public static ProjectInfo getProjectInfo(Object parsedJson, ProjectInfoRepository repository) {
        String projectInfoURI = JsonPath.read(parsedJson, "$._links.self.href");
        var projectInfoURIInSplit = projectInfoURI.split("/");
        var projectInfoId = Long.parseLong(projectInfoURIInSplit[projectInfoURIInSplit.length - 1]);
        return repository.findById(projectInfoId)
                .orElseThrow(() -> new RuntimeException("ProjectInfo not found."));
    }
}
