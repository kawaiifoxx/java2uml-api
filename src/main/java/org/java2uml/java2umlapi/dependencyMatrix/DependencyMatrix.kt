package org.java2uml.java2umlapi.dependencyMatrix

/**
 * This is main entry point for generating a dependency matrix.
 *
 * @author kawaiifoxx
 * @since 1.2.0
 */
interface DependencyMatrix {
    interface DependencyArray {
        operator fun get(i: String): Int
    }

    operator fun get(i: String): DependencyArray
}