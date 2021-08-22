package org.java2uml.java2umlapi.dependencyMatrix

import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver
import org.apache.commons.io.FileDeleteStrategy
import org.assertj.core.api.Assertions.assertThat
import org.java2uml.java2umlapi.parsedComponent.ParsedComponent
import org.java2uml.java2umlapi.parsedComponent.ParsedCompositeComponent
import org.java2uml.java2umlapi.parsedComponent.TypeRelation
import org.java2uml.java2umlapi.parser.Parser
import org.java2uml.java2umlapi.util.unzipper.Unzipper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Path

@DisplayName("When using DependencyMatrix,")
internal class DefaultDependencyMatrixTest {

    private val testFile = "src/test/testSources/ParserTest/addtionalTests/combat-zone-master.zip"
    private val dst = "src/test/testOutput"
    private lateinit var dependencyMatrix: DependencyMatrix
    private lateinit var relationList: List<TypeRelation>

    @BeforeEach
    fun setUp() {
        val dst = Unzipper.unzipDir(Path.of(testFile), Path.of(dst))
        val sC = Parser.parse(dst.toPath())
        val allTypes = mutableListOf<ParsedCompositeComponent>()
        allTypes.addAll(sC.children.values.map(ParsedComponent::asParsedCompositeComponent).map { it.get() })
        allTypes.addAll(sC.externalComponents.values.map(ParsedComponent::asParsedCompositeComponent).map { it.get() })

        dependencyMatrix = DefaultDependencyMatrix(allTypes, sC.relationsList)
        relationList = sC.relationsList
    }

    @Test
    @DisplayName("using get, should return all the values same as number of relations.")
    fun `should return all the values same as number of relations`() {
        val relationMap = relationList.groupBy { it }.map { Pair(it.key, it.value.size) }.toMap()

        for ((relation, value) in relationMap)
            assertThat(dependencyMatrix[relation.to.name][relation.from.name]).isEqualTo(value)
    }

    @AfterEach
    @Throws(IOException::class)
    fun tearDown() {
        JarTypeSolver.ResourceRegistry.getRegistry().cleanUp()
        FileDeleteStrategy.FORCE.delete(Path.of(dst).toFile())
    }
}