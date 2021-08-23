package org.java2uml.java2umlapi.dependencyMatrix

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.java2uml.java2umlapi.dependencyMatrix.DependencyMatrix.DependencyArray
import org.java2uml.java2umlapi.parsedComponent.ParsedCompositeComponent
import org.java2uml.java2umlapi.parsedComponent.TypeRelation

/**
 * This is the default implementation for [DependencyMatrix]
 *
 * @author kawaiifoxx
 * @since 1.2.0
 */
@Suppress("MemberVisibilityCanBePrivate")
@JsonIgnoreProperties(ignoreUnknown = true)
class DefaultDependencyMatrix(compositeComponents: List<ParsedCompositeComponent>, relations: List<TypeRelation>) :
    DependencyMatrix {

    /**
     * Default Implementation for [DependencyArray]
     *
     * @author kawaiifoxx
     * @since 1.2.0
     */
    @Suppress("MemberVisibilityCanBePrivate")
    class DefaultDependencyArray(private val componentToIndexMap: Map<String, Int>) :
        DependencyArray {

        /**
         * Which classes use this class as dependency.
         */
        val usedBy = IntArray(componentToIndexMap.size)


        override operator fun get(i: String): Int {
            if (!componentToIndexMap.contains(i))
                throw IllegalArgumentException("mapping for given ParsedCompositeComponent is not present.")

            return usedBy[componentToIndexMap[i]!!]
        }

        operator fun set(i: String, usage: Int) {
            if (!componentToIndexMap.contains(i))
                throw IllegalArgumentException("mapping for given ParsedCompositeComponent is not present.")

            usedBy[componentToIndexMap[i]!!] = usage
        }
    }

    private val packageTree: PackageTree = DefaultPackageTree(compositeComponents)
    val componentToIndexMap = packageTree.componentToIndexMap
    val dependencyMatrix = Array(compositeComponents.size) { DefaultDependencyArray(componentToIndexMap) }

    init {
        for (relation in relations) {
            if (!componentToIndexMap.contains(relation.from.name) || !componentToIndexMap.contains(relation.to.name))
                continue

            dependencyMatrix[componentToIndexMap[relation.to.name]!!][relation.from.name]++
        }
    }

    override fun get(i: String): DependencyArray {
        if (!componentToIndexMap.contains(i)) throw IllegalArgumentException("no mapping present for $i")

        return dependencyMatrix[componentToIndexMap[i]!!]
    }
}