package org.java2uml.java2umlapi.callGraph;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * MethodCallGraph is used for generation of a call graph for method calls.
 * </p>
 *
 * @author kawaiifox
 */
public interface MethodCallGraph {
    /**
     * Get a concatenated representation of call graph.
     *
     * @return returns a representation of call graph in string form.
     */
    String getPlantUMLMindMap();

    /**
     * Get a mapping from methodName (String) -> neighbors (List<String>)
     *
     * @return map from methodName to all its neighbors.
     */
    Map<String, List<String>> getCallGraphMap();

    /**
     * Generates the call graph and returns the root node.
     *
     * @return root node.
     */
    CallGraphNode getCallGraph();

    /**
     * Get a list of relations for call graphs.
     * <p>
     *<pre>
     * Example: For this.
     *
     * void methodA() {
     *      methodB();
     *      methodC();
     *      methodA();
     * }
     *
     * you should get relations below in the list.
     *
     * CallGraphRelation{From -> to}
     * List {
     *      CallGraphRelation{methodA() -> methodB()},
     *      CallGraphRelation{methodA() -> methodC()},
     *      CallGraphRelation{methodA() -> methodA()}
     * }
     * </pre>
     *
     * @param methodSigToIdMap a map with method id and signature mappings.
     * @return List of class relations.
     */
    List<CallGraphRelation> getCallGraphRelations(Map<String, Long> methodSigToIdMap);

}
