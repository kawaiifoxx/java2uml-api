package org.java2uml.java2umlapi.restControllers;

import com.jayway.jsonpath.JsonPath;
import org.java2uml.java2umlapi.fileStorage.entity.ProjectInfo;
import org.java2uml.java2umlapi.fileStorage.exceptions.MyFileNotFoundException;
import org.java2uml.java2umlapi.fileStorage.repository.ProjectInfoRepository;
import org.java2uml.java2umlapi.fileStorage.service.UnzippedFileStorageService;
import org.java2uml.java2umlapi.parsedComponent.SourceComponent;
import org.java2uml.java2umlapi.parsedComponent.service.SourceComponentService;
import org.java2uml.java2umlapi.restControllers.exceptions.ProjectInfoNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.java2uml.java2umlapi.restControllers.ControllerTestUtils.CONTENT_TYPE;
import static org.java2uml.java2umlapi.restControllers.ControllerTestUtils.TEST_FILE_1;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Tag("WebApiTest")
@DisplayName("When using ProjectInfo,")
class ProjectInfoControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    UnzippedFileStorageService fileStorageService;
    @Autowired
    ProjectInfoRepository repository;
    @Autowired
    SourceComponentService sourceComponentService;


    @Test
    @DisplayName("provided that request is valid, response status should be 200 OK," +
            " and response should have valid details.")
    void testOne() throws Exception {
        var responseFromFileController = ControllerTestUtils
                .doMultipartRequest(mvc, TEST_FILE_1)
                .andReturn()
                .getResponse()
                .getContentAsString();

        String projectInfoURI = JsonPath.read(responseFromFileController, "$._links.self.href");

        var responseFromProjectInfoController = mvc.perform(get(projectInfoURI))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectName", is("test1.zip")))
                .andExpect(jsonPath("$.fileType", is(CONTENT_TYPE)))
                .andExpect(jsonPath("$._links.self.href", containsString("/api/project-info/")))
                .andExpect(jsonPath("$._links.umlSvg.href", containsString("/api/uml/svg/")))
                .andExpect(jsonPath("$._links.umlText.href", containsString("/api/uml/plant-uml-code/")))
                .andExpect(jsonPath("$._links.projectModel.href",
                        containsString("/api/source/by-project-info/")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(responseFromProjectInfoController).isEqualTo(responseFromFileController);
    }

    @Test
    @DisplayName("sending delete request at /api/project-info/{projectInfoId} should delete all the related resources.")
    void testDelete() throws Exception {
        var response = ControllerTestUtils.doMultipartRequest(mvc, TEST_FILE_1).andReturn().getResponse().getContentAsString();
        String projectInfoUri = JsonPath.read(response, "$._links.delete.href");
        ProjectInfo projectInfo = getProjectInfo(projectInfoUri);
        mvc.perform(delete(projectInfoUri)).andDo(print()).andExpect(status().isNoContent());
        assertThatSourceComponentIsNotPresent(projectInfo);
        assertThatUnzippedFileIsDeleted(projectInfo);
    }

    /**
     * Checks that if unzipped file is deleted from {@link UnzippedFileStorageService}, if not then fails.
     * @param projectInfo {@link ProjectInfo} from which unzipped file name will be fetched.
     */
    private void assertThatUnzippedFileIsDeleted(ProjectInfo projectInfo) {
        assertThatThrownBy(() -> fileStorageService.find(projectInfo.getUnzippedFileName()))
                .describedAs("Unzipped file should be deleted")
                .isInstanceOf(MyFileNotFoundException.class);
    }

    /**
     * checks whether the {@link SourceComponent} is present, if not present then fail the test.
     * @param projectInfo {@link ProjectInfo} from which {@link SourceComponent} id is fetched.
     */
    private void assertThatSourceComponentIsNotPresent(ProjectInfo projectInfo) {
        sourceComponentService.get(projectInfo.getSourceComponentId()).
                ifPresent(sourceComponent -> fail("Source Component should have been deleted."));
    }

    /**
     * Fetches {@link ProjectInfo} from {@link ProjectInfoRepository}.
     *
     * @param projectInfoUri URI of {@link ProjectInfo} to be fetched.
     * @return {@link ProjectInfo}
     */
    private ProjectInfo getProjectInfo(String projectInfoUri) {
        var splitUrl = projectInfoUri.split("/");
        var projectInfoId = splitUrl[splitUrl.length - 1];
        return repository.findById(Long.parseLong(projectInfoId)).orElseThrow(
                () -> new ProjectInfoNotFoundException("Unable to fetch project info with id " + projectInfoId)
        );
    }
}