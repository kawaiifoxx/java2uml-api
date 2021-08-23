package org.java2uml.java2umlapi.restControllers.dependencyMatrix

import net.minidev.json.JSONArray
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.CoreMatchers.containsString
import org.java2uml.java2umlapi.fileStorage.entity.ProjectInfo
import org.java2uml.java2umlapi.fileStorage.repository.ProjectInfoRepository
import org.java2uml.java2umlapi.restControllers.ControllerTestUtils.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@SpringBootTest
@AutoConfigureMockMvc
@Tag("WebApiTest")
@DisplayName("When using DependencyMatrixController,")
@DirtiesContext
internal class DependencyMatrixControllerTest {
    @Autowired
    var mvc: MockMvc? = null

    @Autowired
    var projectInfoRepository: ProjectInfoRepository? = null

    lateinit var projectInfo: ProjectInfo

    @BeforeEach
    fun setUp() {
        assertThat(mvc).isNotNull
        assertThat(projectInfoRepository).isNotNull

        val parsedJson = parseJson(getMultipartResponse(doMultipartRequest(mvc, JAVA2UML_API_SOURCE)))
        projectInfo = getEntityFromJson(parsedJson, projectInfoRepository)
    }

    @Test
    @DisplayName("http get on /api/dependency-matrix should generate dependency matrix.")
    internal fun getDependencyMatrix() {
        waitTillResourceGetsGenerated(mvc, "$URI${projectInfo.id}")
        val result = mvc?.perform(get("$URI${projectInfo.id}"))
            ?.andExpect(status().isOk)
            ?.andExpect(jsonPath("$._links.self.href", containsString("$URI${projectInfo.id}")))
            ?.andExpect(jsonPath("$._links.projectInfo.href", containsString("/project-info/")))
            ?.andReturn()?.response?.contentAsString

        val parsedJson = parseJson(result) as Map<*, *>
        assertThat(parsedJson["componentToIndexMap"]).isInstanceOf(Map::class.java)
        assertThat(parsedJson["dependencyMatrix"]).isInstanceOf(JSONArray::class.java)
    }

    companion object {
        @AfterAll
        @JvmStatic
        fun tearDown() = cleanUp()

        const val URI = "/api/dependency-matrix/"
    }
}