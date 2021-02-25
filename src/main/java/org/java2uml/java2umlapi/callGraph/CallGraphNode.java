package org.java2uml.java2umlapi.callGraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * CallGraphNode is a node in the call graph of method calls.
 * </p>
 * @see MethodCallGraph
 * @author kawaiifox
 */
public class CallGraphNode {
    private final String name;
    private final List<CallGraphNode> neighbors;
    private boolean selfLoop = false;

    /**
     * Initializes CallGraphNode with a name and a empty list of neighbors.
     * @param name Qualified signature of method, should be unique.
     */
    public CallGraphNode(String name) {
        this.name = name;
        neighbors = new ArrayList<>();
    }

    /**
     * @return name of this node.
     */
    public String getName() {
        return name;
    }

    /**
     * @param node neighbor you want to add to the neighbor list of this node.
     */
    public void addNeighbor(CallGraphNode node) {
        neighbors.add(node);
    }

    /**
     * @return a list of all neighbors of this node.
     */
    public List<CallGraphNode> getNeighbors() {
        return neighbors;
    }

    /**
     * @return true if this node has a self loop.
     */
    public boolean isSelfLoop() {
        return selfLoop;
    }

    /**
     * @param selfLoop set it to true if method that this node represents is recursive.
     */
    public void setSelfLoop(boolean selfLoop) {
        this.selfLoop = selfLoop;
    }

    /**
     * Generates plant uml mind map code from this node as root.
     * @return Mind Map code which can be parsed plant uml.
     */
    public String pUmlCode() {
        return "@startmindmap\n" + pUmlCode(new HashSet<>(), "*") + "@endmindmap";
    }

    /**
     * Performs the dfs and generates plant uml mind map code.
     * @param visited set of already visited CallGraphNodes.(just pass empty set if you are calling this method.)
     * @param depth depth at which the recursion is at,  e.g. * for level 1, ** for level 2 and so..on.
     * @return Mind Map code without @startmindmap and @endmindmap.
     */
    private String pUmlCode(Set<String> visited, String depth) {
        String code = "";

        if (!visited.contains(getName())) {
            visited.add(getName());
            code += depth + " " + (selfLoop ? "<&reload> " : "") + getName() + "\n"
                    + neighbors.stream()
                    .map(neighbor -> neighbor.pUmlCode(visited, depth + "*"))
                    .collect(Collectors.joining());
        } else {
            code += depth + " " + (selfLoop ? "<&reload> " : "") + getName() + "\n";
        }
        return code;
    }

    @Override
    public String toString() {
        return "CallGraphNode{" +
                "name='" + name + '\'' +
                ", selfLoop=" + selfLoop +
                '}';
    }
}
