package org.java2uml.java2umlapi.dependencyMatrix

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.java2uml.java2umlapi.parsedComponent.ParsedCompositeComponent

/**
 * Default Implementation for {@link PackageTree} class
 *
 * @author kawaiifoxx
 * @since 1.2.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class DefaultPackageTree(compositeComponents: List<ParsedCompositeComponent>) : PackageTree {
    /**
     * Entity representing a tree node.
     * @author kawaiifoxx
     */
    private data class Node(
        val name: String = "",
        var range: IntRange = 0..0,
        var size: Int = 0,
        var isCompositeComponent: Boolean = false,
        val children: MutableMap<String, Node> = HashMap()
    ) {
        operator fun get(i: String) = children[i]
        operator fun set(i: String, node: Node) {
            children[i] = node
        }
    }

    private val root: Node = addAll(compositeComponents)

    @JsonIgnore
    private var _componentToIndexMap: MutableMap<String, Int>? = null

    override val componentToIndexMap: Map<String, Int>
        @JsonIgnore
        get() {
            if (_componentToIndexMap != null) return _componentToIndexMap!!
            _componentToIndexMap = mutableMapOf()
            addAllComponents()
            return _componentToIndexMap!!
        }

    override fun getRange(packageOrCompositeComponentName: String) =
        traverse(packageOrCompositeComponentName.split('.')).range

    override fun getSize(packageOrCompositeComponentName: String) =
        traverse(packageOrCompositeComponentName.split('.')).size


    override fun contains(packageOrCompositeComponentName: String) =
        try {
            traverse(packageOrCompositeComponentName.split('.'));true
        } catch (e: IllegalArgumentException) {
            false
        }

    /**
     * Used for initialising root node and generating the whole tree.
     *
     * @param compositeComponents  is a list of ParsedCompositeComponent
     * @return Root of the tree.
     */
    private fun addAll(compositeComponents: List<ParsedCompositeComponent>): Node {
        val root = Node("", compositeComponents.indices)

        for (compositeComponent in compositeComponents) {
            var temp = root
            val path = compositeComponent.name.split('.')

            for (edge in path) {
                if (edge.isBlank()) throw IllegalArgumentException("Class name is incorrect")

                if (temp[edge] == null) {
                    temp[edge] = Node(edge)
                }

                temp = temp[edge]!!
            }
            temp.isCompositeComponent = true
            temp.size = 1
        }

        updateSize(root)
        updateRanges(root)

        return root
    }

    private fun updateSize(root: Node): Int {
        for (child in root.children.values)
            root.size += updateSize(child)

        return root.size
    }

    private fun updateRanges(root: Node) {
        var start = if (root.isCompositeComponent) root.range.first + 1 else root.range.first

        for (child in root.children.values) {
            child.range = start until start + child.size
            start += child.size
            updateRanges(child)
        }
    }

    /**
     * Traverses the root node to find the node with given path.
     * @param path to be traversed
     * @return Node with given path
     */
    private fun traverse(path: List<String>): Node {
        var itr = root

        for (edge in path) {
            if (itr[edge] == null)
                throw IllegalArgumentException("The class or package you are looking for is not present in PackageTree")

            itr = itr[edge]!!
        }

        return itr
    }

    /**
     * Traverses the Package tree and adds mapping for leaves to indices.
     */
    private fun addAllComponents(currNode: Node = root, path: StringBuilder = StringBuilder()) {
        path.append(currNode.name)

        if (currNode.isCompositeComponent)
            _componentToIndexMap!![path.toString().trim('.')] = currNode.range.first

        for (child in currNode.children.values)
            addAllComponents(child, StringBuilder(path).append('.'))
    }
}