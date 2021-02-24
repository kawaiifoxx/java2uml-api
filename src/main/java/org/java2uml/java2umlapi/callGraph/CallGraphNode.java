package org.java2uml.java2umlapi.callGraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CallGraphNode {
    private final String name;
    private final List<CallGraphNode> neighbors;
    private boolean selfLoop = false;

    public CallGraphNode(String name) {
        this.name = name;
        neighbors = new ArrayList<>();
    }


    public String getName() {
        return name;
    }

    public void addNeighbor(CallGraphNode node) {
        neighbors.add(node);
    }

    public List<CallGraphNode> getNeighbors() {
        return neighbors;
    }

    public boolean isSelfLoop() {
        return selfLoop;
    }

    public void setSelfLoop(boolean selfLoop) {
        this.selfLoop = selfLoop;
    }

    public String pUmlCode() {
        return "@startmindmap\n" + pUmlCode(new HashSet<>(), "*") + "@endmindmap";
    }

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
