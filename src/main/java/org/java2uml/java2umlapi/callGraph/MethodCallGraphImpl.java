package org.java2uml.java2umlapi.callGraph;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * This class is implementation of MethodCallGraph interface.
 * </p>
 *
 * @author kawaiifox
 */
public class MethodCallGraphImpl implements MethodCallGraph {

    private final ResolvedMethodDeclaration root;
    private CallGraphNode rootNode = null;
    private final String basePackage;

    /**
     * Constructor for MethodCallGraphImpl, it takes in ResolvedMethodDeclaration and name of the base package
     *
     * @param root        ResolvedMethodDeclaration of method for which call graph needs to be generated.
     * @param basePackage name of the base package for limiting the call graph scope to that package and its, sub packages.
     */
    public MethodCallGraphImpl(ResolvedMethodDeclaration root, String basePackage) {
        this.basePackage = basePackage;
        this.root = root;
    }

    /**
     * Get a concatenated representation of call graph.
     *
     * @return returns a representation of call graph in string form.
     */
    @Override
    public String getPlantUMLMindMap() {
        return getCallGraph().pUmlCode();
    }

    /**
     * Get a mapping from methodName (String) -> neighbors (List<String>)
     *
     * @return map from methodName to all its neighbors.
     */
    @Override
    public Map<String, List<String>> getCallGraphMap() {
        var map = generateCallGraphMap(getCallGraph(), new HashSet<>());
        map.put(rootNode.getName(), rootNode.getNeighbors()
                .stream()
                .map(CallGraphNode::getName)
                .collect(Collectors.toList()));

        if (rootNode.isSelfLoop()) {
            map.get(rootNode.getName()).add(rootNode.getName());
        }

        return map;
    }

    /**
     * Performs a dfs on currentNode and generates a Map of  nodeName(String) -> neighbors(List<String>))
     * @param currentNode node on which dfs needs to be done.
     * @param visited already visited nodes.
     * @return a map of nodeName(String) -> neighbors(List<String>).
     */
    private Map<String, List<String>> generateCallGraphMap(CallGraphNode currentNode, Set<String> visited) {
        visited.add(currentNode.getName());

        return currentNode.getNeighbors()
                .stream()
                .filter(neighbor -> !visited.contains(neighbor.getName()))
                .map(neighbor -> {
                    var map = generateCallGraphMap(neighbor, visited);
                    map.put(neighbor.getName(),
                            neighbor.getNeighbors()
                                    .stream()
                                    .map(CallGraphNode::getName)
                                    .collect(Collectors.toList()));

                    if (neighbor.isSelfLoop()) {
                        map.get(neighbor.getName()).add(neighbor.getName());
                    }
                    return map;
                })
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    }


    /**
     * Generates the call graph and returns the root node.
     *
     * @return root node.
     */
    @Override
    public CallGraphNode getCallGraph() {
        if (rootNode == null) {
            rootNode = new CallGraphNode(root.getQualifiedSignature());
            buildCallGraph(rootNode, root, new HashMap<>());
            return rootNode;
        }

        return rootNode;
    }

    public List<CallGraphRelation> getCallGraphRelations(Map<String, Long> methodSigToIdMap) {
        return getCallGraphMap().entrySet()
                .stream()
                .map(entry -> {
                    var from = entry.getKey();
                    return entry.getValue()
                            .stream()
                            .map(to -> new CallGraphRelation(from, to))
                            .collect(Collectors.toList());
                })
                .flatMap(Collection::stream)
                .peek(relation -> {
                    relation.setFromId(methodSigToIdMap.getOrDefault(relation.getFrom(), null));
                    relation.setToId(methodSigToIdMap.getOrDefault(relation.getTo(), null));
                })
                .collect(Collectors.toList());
    }

    /**
     * Performs dfs and builds call graph.
     *
     * @param currNode                  root node of tree which is currently being built.
     * @param resolvedMethodDeclaration resolved method declaration for currNode.
     * @param cache                     set containing unique names of already cache nodes. (Just Pass an empty set if you are calling this method.)
     */
    private void buildCallGraph(CallGraphNode currNode, ResolvedMethodDeclaration resolvedMethodDeclaration, Map<String, CallGraphNode> cache) {
        cache.put(currNode.getName(), currNode);
        getResolvedMethodDeclarationStream(resolvedMethodDeclaration)
                .filter(rMd -> {
                    if (rMd.getQualifiedSignature().equals(currNode.getName())) {
                        currNode.setSelfLoop(true);
                        return false;
                    }
                    return true;
                })
                .forEach(rMd -> {
                    if (!cache.containsKey(rMd.getQualifiedSignature())) {
                        var neighborNode = new CallGraphNode(rMd.getQualifiedSignature());
                        currNode.addNeighbor(neighborNode);
                        buildCallGraph(neighborNode, rMd, cache);
                    } else
                        currNode.addNeighbor(cache.get(rMd.getQualifiedSignature()));
                });
    }


    /**
     * Generates the set of resolvedMethodDeclarations from methods present in the call graph.
     *
     * @return Set of resolvedMethodDeclarations.
     */
    public Set<ResolvedMethodDeclaration> getAllResolvedMethodDeclarations() {
        var set = getSetOfRMD(root, new HashSet<>());
        set.add(root);
        return set;
    }

    /**
     * Performs DFS on given resolvedMethodDeclaration and returns a set of ResolvedMethodDeclaration, excluding given
     * resolvedMethodDeclaration.
     * @param resolvedMethodDeclaration resolvedMethodDeclaration of which you want all the children of.
     * @param visited contains names of already visited resolvedMethodDeclarations. (just pass an empty set if you are calling this method.)
     * @return Set of resolvedMethodDeclaration.
     */
    private Set<ResolvedMethodDeclaration> getSetOfRMD(ResolvedMethodDeclaration resolvedMethodDeclaration, Set<String> visited) {
        visited.add(resolvedMethodDeclaration.getQualifiedSignature());

        return getResolvedMethodDeclarationStream(resolvedMethodDeclaration)
                .filter(rMd -> !visited.contains(rMd.getQualifiedSignature()) && rMd.getQualifiedSignature().startsWith(basePackage))
                .map(rMd -> {
                    var set = getSetOfRMD(rMd, visited);
                    set.add(rMd);
                    return set;
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    /**
     * Takes in resolvedMethodDeclaration and finds resolvedMethodDeclarations of methods
     * that are present in the body of passed resolvedMethodDeclaration and returns a
     * stream containing these resolvedMethodDeclarations.
     * @param resolvedMethodDeclaration resolvedMethodDeclaration of which you want to find all resolvedMethodDeclarations
     *                                  of method calls present in body of the passed resolvedMethodDeclaration.
     * @return stream of resolvedMethodDeclarations
     */
    private Stream<ResolvedMethodDeclaration> getResolvedMethodDeclarationStream(ResolvedMethodDeclaration resolvedMethodDeclaration) {
        return resolvedMethodDeclaration
                .toAst()
                .orElseThrow(() -> new RuntimeException("unable to get AST from ResolvedMethodDeclaration."))
                .findAll(MethodCallExpr.class)
                .stream()
                .map(MethodCallExpr::resolve);
    }
}
