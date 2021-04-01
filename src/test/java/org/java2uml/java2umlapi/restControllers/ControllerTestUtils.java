package org.java2uml.java2umlapi.restControllers;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import org.java2uml.java2umlapi.fileStorage.entity.ProjectInfo;
import org.java2uml.java2umlapi.fileStorage.repository.ProjectInfoRepository;
import org.java2uml.java2umlapi.lightWeight.LightWeight;
import org.java2uml.java2umlapi.lightWeight.Source;
import org.java2uml.java2umlapi.lightWeight.repository.SourceRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    public static final Path ENUM_LW_CONTROLLER_TEST_FILE = Path.of(
            "src/test/testSources/WebApiTest/EnumLWControllerTest/ForEnumControllerTest.zip"
    );
    public static final String CONTENT_TYPE = "application/zip";

    /**
     * Performs a multipart request with ControllerTestUtils.URI.
     *
     * @param mvc  {@link MockMvc}
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
     * @param <T>        Type of entity.
     * @return Entity of type T from {@link CrudRepository}{@code <T, Long>} with {@code <T>} and  {@link Long}.
     */
    public static <T> T getEntityFromJson(Object parsedJson, CrudRepository<T, Long> repository) {
        return repository.findById(getIdFromSelfLink(parsedJson))
                .orElseThrow(() -> new RuntimeException("Entity not found."));
    }

    /**
     * @param parsedJson from which id will be retrieved.
     * @return id from the self link.
     * @throws NumberFormatException if URI cannot be parsed for getting id.
     */
    private static long getIdFromSelfLink(Object parsedJson) {
        String projectInfoURI = JsonPath.read(parsedJson, "$._links.self.href");
        var projectInfoURIInSplit = projectInfoURI.split("/");
        return Long.parseLong(projectInfoURIInSplit[projectInfoURIInSplit.length - 1]);
    }

    /**
     * Parses given string and returns an linked hash map with key value pairs.
     *
     * @param unparsed Json you want to parse.
     * @return parsed json.
     */
    public static Object parseJson(String unparsed) {
        return Configuration.defaultConfiguration().jsonProvider().parse(unparsed);
    }

    /**
     * Fetches {@link Source} from the {@link SourceRepository}.
     *
     * @param mvc        to perform get requests.
     * @param repository from which {@link Source} will be fetched.
     * @param file       to be sent in the multipart request.
     * @return {@link Source}
     */
    public static Source getSource(MockMvc mvc, SourceRepository repository, Path file) throws Exception {
        var parsedProjectInfo = parseJson(getMultipartResponse(doMultipartRequest(mvc, file)));
        var parsedSourceJson = parseJson(
                mvc.perform(get("" + JsonPath.read(parsedProjectInfo, "$._links.projectModel.href")))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString()
        );
        return getEntityFromJson(parsedSourceJson, repository);
    }

    /**
     * Asserts that on performing a get request on given uri the provided exception is thrown.
     *
     * @param mvc             {@link MockMvc} through which request will be performed.
     * @param uri             on which get request will be performed.
     * @param exceptionToTest {@link Class} of the exception you want to check whether it is thrown.
     * @return ResultAction in case you want to perform more checks.
     * @throws Exception If unable to perform get request.
     */
    public static ResultActions assertThatOnPerformingGetProvidedExceptionIsThrown(
            MockMvc mvc, String uri, Class<? extends Exception> exceptionToTest
    ) throws Exception {
        var resultAction = mvc.perform(get(uri))
                .andDo(print());
        var e = resultAction.andReturn().getResolvedException();
        assertThat(e).isNotNull().isInstanceOf(exceptionToTest);
        return resultAction;
    }

    /**
     * Performs a get request on "/api/constructor/by-parent/{parentId}"
     *
     * @param mvc        through which get request will be performed.
     * @param uri        on which get will be performed.
     * @param parentLink to test.
     * @return Parsed Json
     */
    public static Object performGetOn(MockMvc mvc, String uri, String parentLink) throws Exception {
        return parseJson(
                mvc.perform(get(uri))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$._links.self.href", containsString(uri)))
                        .andExpect(jsonPath("$._links.parent.href",
                                containsString(parentLink)))
                        .andReturn().getResponse().getContentAsString()
        );
    }

    /**
     * Asserts that all the constructor names match up with the names of provided constructors.
     *
     * @param parsedJson   Json, from this json all the names will be extracted.
     * @param jsonPath     should target a list of strings.
     * @param lightWeights {@link List} of {@link LightWeight}
     * @throws ClassCastException if jsonPath does not target a list of strings.
     * @throws AssertionError if jsonPath does not target names of the lightweights.
     */
    public static void assertThatAllNamesMatch(Object parsedJson, String jsonPath, List<? extends LightWeight> lightWeights) {
        List<String> constructorNames = JsonPath.read(parsedJson, jsonPath);
        assertThat(new HashSet<>(constructorNames))
                .isEqualTo(lightWeights.stream().map(LightWeight::getName).collect(Collectors.toSet()));
    }
}
