package org.java2uml.java2umlapi.dependencyMatrix

/**
 * PackageTree represents package hierarchy for parsed Project.
 * Main purpose of this class is to distribute DependencyMatrix indices across classes.
 *
 * @author kawaiifoxx
 * @since 1.2.0
 */
interface PackageTree {
    val componentToIndexMap: Map<String, Int>

    /**
     * @return indices Range for given package
     */
    fun getRange(packageOrCompositeComponentName: String): IntRange

    /**
     * @return size of given package or 1 in case of class or interface or enum.
     */
    fun getSize(packageOrCompositeComponentName: String): Int

    /**
     * answers question given below.
     * is given packageOrCompositeComponent present in this PackageTree?
     */
    fun contains(packageOrCompositeComponentName: String): Boolean
}