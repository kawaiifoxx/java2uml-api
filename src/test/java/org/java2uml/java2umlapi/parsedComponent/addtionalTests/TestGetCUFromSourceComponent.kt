package org.java2uml.java2umlapi.parsedComponent.addtionalTests

import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver
import org.apache.commons.io.FileDeleteStrategy
import org.assertj.core.api.Assertions.assertThat
import org.java2uml.java2umlapi.parsedComponent.SourceComponent
import org.java2uml.java2umlapi.parser.Parser
import org.java2uml.java2umlapi.util.unzipper.Unzipper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Path

@DisplayName("When using SourceComponent, ")
class TestGetCUFromSourceComponent {

    companion object {
        private const val TEST_FILE = "src/test/testSources/WebApiTest/java2uml-api-no-dep.zip"
        private const val DST = "src/test/testOutput"
    }

    lateinit var sourceComponent: SourceComponent

    @BeforeEach
    internal fun setUp() {
        val destDir = Unzipper.unzipDir(Path.of(TEST_FILE), Path.of(DST))
        sourceComponent = Parser.parse(destDir.toPath())
    }

    @Test
    @DisplayName("on calling getCompilationUnits(), should get a list of compilation units.")
    internal fun `can we get compilationUnit from SourceComponent`() {
        val cus = sourceComponent.compilationUnits
        assertThat(cus).isNotEmpty
    }

    @AfterEach
    @Throws(IOException::class)
    fun tearDown() {
        JarTypeSolver.ResourceRegistry.getRegistry().cleanUp()
        FileDeleteStrategy.FORCE.delete(Path.of(DST).toFile())
    }
}