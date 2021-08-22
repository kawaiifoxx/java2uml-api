package org.java2uml.java2umlapi.dependencyMatrix

import org.java2uml.java2umlapi.dependencyMatrix.DependencyMatrix.DependencyArray
import org.java2uml.java2umlapi.parsedComponent.ParsedCompositeComponent

/**
 * This is the default implementation for {@link DependencyMatrix}
 *
 * @author kawaiifoxx
 * @since 1.2.0
 */
class DefaultDependencyMatrix(compositeComponents: List<ParsedCompositeComponent>) : DependencyMatrix {

    /**
     * Default Implementation for {@link DependencyArray}
     *
     * @author kawaiifoxx
     * @since 1.2.0
     */
    private class DefaultDependencyArray(private val componentToIndexMap: Map<String, Int>) :
        DependencyArray {

        /**
         * Which classes use this class as dependency.
         */
        private val usedBy = IntArray(componentToIndexMap.size)


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

    override fun get(i: String): DependencyArray {
        TODO("Not yet implemented")
    }
}