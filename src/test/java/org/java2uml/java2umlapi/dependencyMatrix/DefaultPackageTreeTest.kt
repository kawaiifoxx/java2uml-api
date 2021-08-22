package org.java2uml.java2umlapi.dependencyMatrix

import org.assertj.core.api.Assertions.assertThat
import org.java2uml.java2umlapi.parsedComponent.ParsedCompositeComponent
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
@DisplayName("When using DefaultPackageTree, ")
internal class DefaultPackageTreeTest {

    lateinit var defaultPackageTree: DefaultPackageTree

    private val classNameList = listOf(
        "org.java2uml.java2umlapi.aspects.EntryExitLogger",
        "org.java2uml.java2umlapi.aspects.InstrumentationLogger",
        "org.java2uml.java2umlapi.aspects.SimpleCallTracer",
        "org.java2uml.java2umlapi.callGraph.CallGraphNode",
        "org.java2uml.java2umlapi.callGraph.CallGraphRelation",
        "org.java2uml.java2umlapi.callGraph.MethodCallGraph",
        "org.java2uml.java2umlapi.callGraph.MethodCallGraphImpl",
        "org.java2uml.java2umlapi.config.AppProperties",
        "org.java2uml.java2umlapi.config.ServerConfig",
        "org.java2uml.java2umlapi.dependencyMatrix.DefaultPackageTree",
        "org.java2uml.java2umlapi.dependencyMatrix.DependencyMatrix",
        "org.java2uml.java2umlapi.dependencyMatrix.PackageTree",
        "org.java2uml.java2umlapi.exceptions.EmptySourceDirectoryException",
        "org.java2uml.java2umlapi.exceptions.OrphanComponentException",
        "org.java2uml.java2umlapi.executor.ExecutorWrapper",
        "org.java2uml.java2umlapi.lightWeight.Body",
        "org.java2uml.java2umlapi.lightWeight.ClassOrInterface",
        "org.java2uml.java2umlapi.lightWeight.ClassRelation",
        "org.java2uml.java2umlapi.lightWeight.Constructor",
        "org.java2uml.java2umlapi.lightWeight.EnumConstant",
        "org.java2uml.java2umlapi.lightWeight.EnumLW",
        "org.java2uml.java2umlapi.lightWeight.Field",
        "org.java2uml.java2umlapi.lightWeight.LightWeight",
        "org.java2uml.java2umlapi.lightWeight.Method",
        "org.java2uml.java2umlapi.lightWeight.Param",
        "org.java2uml.java2umlapi.lightWeight.Source",
        "org.java2uml.java2umlapi.lightWeight.SpecifiedException",
        "org.java2uml.java2umlapi.lightWeight.repository.BodyRepository",
        "org.java2uml.java2umlapi.lightWeight.repository.ClassOrInterfaceRepository",
        "org.java2uml.java2umlapi.lightWeight.repository.ClassRelationRepository",
        "org.java2uml.java2umlapi.lightWeight.repository.ConstructorRepository",
        "org.java2uml.java2umlapi.modelAssemblers.BodyAssembler",
        "org.java2uml.java2umlapi.modelAssemblers.CallGraphRelationAssembler",
        "org.java2uml.java2umlapi.modelAssemblers.ClassOrInterfaceAssembler",
        "org.java2uml.java2umlapi.modelAssemblers.ClassRelationAssembler",
        "org.java2uml.java2umlapi.modelAssemblers.ConstructorAssembler",
        "org.java2uml.java2umlapi.modelAssemblers.EnumConstantAssembler",
        "org.java2uml.java2umlapi.modelAssemblers.EnumLWAssembler",
        "org.java2uml.java2umlapi.modelAssemblers.FieldAssembler",
        "org.java2uml.java2umlapi.modelAssemblers.MethodAssembler",
        "org.java2uml.java2umlapi.modelAssemblers.ProjectInfoAssembler",
        "org.java2uml.java2umlapi.modelAssemblers.SourceAssembler"
    )

    @BeforeEach
    fun setUp() {
        defaultPackageTree = DefaultPackageTree(classNameList.map {
            val pcc = mock(ParsedCompositeComponent::class.java)
            doReturn(it).`when`(pcc).name
            pcc
        })
    }

    @Test
    @DisplayName("using getRange should return the range for given package or class")
    internal fun getRange() {
        val range = defaultPackageTree.getRange("org")
        assertThat(range).isEqualTo(classNameList.indices)
    }

    @Test
    @DisplayName("using getRange two sibling packages should return disjoint ranges.")
    internal fun twoDisjointRanges() {
        val range1 = defaultPackageTree.getRange("org.java2uml.java2umlapi.callGraph")
        val range2 = defaultPackageTree.getRange("org.java2uml.java2umlapi.lightWeight")
        assertThat(range1.intersect(range2)).isEmpty()
    }

    @Test
    @DisplayName("using getRange from parent and child packages should return intersecting ranges.")
    internal fun twoIntersectingRanges() {
        val parentRange = defaultPackageTree.getRange("org.java2uml.java2umlapi.lightWeight")
        val childRange = defaultPackageTree.getRange("org.java2uml.java2umlapi.lightWeight.repository")
        assertThat(parentRange.intersect(childRange)).isNotEmpty
    }

    @Test
    @DisplayName("using getSize, Should return size of given package.")
    internal fun getSize() {
        assertThat(defaultPackageTree.getSize("org")).isEqualTo(classNameList.size)
    }

    @Test
    @DisplayName("using contains(), should return a boolean value indicating whether given package is present or not.")
    internal fun contains() {
        classNameList.forEach {
            assertThat(defaultPackageTree.contains(it)).isTrue
        }
    }

    @Test
    @DisplayName("using contains(), should return false when querying values, which are not present in PackageTree")
    internal fun `should return false when querying values, which are not present in PackageTree`() {
        assertThat(defaultPackageTree.contains("org.notPresent.in.this.PackageTree")).isFalse
    }
}