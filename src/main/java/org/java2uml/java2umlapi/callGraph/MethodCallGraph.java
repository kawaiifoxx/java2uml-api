package org.java2uml.java2umlapi.callGraph;

import org.java2uml.java2umlapi.umlComponenets.ParsedComponent;

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

    String getCallGraphString();

    Map<String, List<String>> getCallGraphMap();

    Map<String, ParsedComponent> getAllMethods();
}
