package org.java2uml.java2umlapi.callGraph;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.SymbolResolver;
import org.java2uml.java2umlapi.exceptions.OrphanComponentException;
import org.java2uml.java2umlapi.umlComponenets.ParsedMethodComponent;
import org.java2uml.java2umlapi.umlComponenets.SourceComponent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * This class is implementation of MethodCallGraph interface.
 * </p>
 *
 * @author kawaiifox
 */
public class MethodCallGraphImpl implements MethodCallGraph {

    private final SourceComponent sourceComponent;
    private final SymbolResolver symbolResolver;
    private final ParsedMethodComponent root;
    private final boolean isRecursive = false;

    /**
     * Constructor for MethodCallGraphImpl, it takes in ParsedMethodComponent and tries to traverse up the tree and
     * get SourceComponent and symbol resolver on it's own.
     *
     * @param root ParsedMethodComponent for which call graph needs to be generated.
     * @throws OrphanComponentException If MethodCallGraph is unable to find parents
     *                                  of given ParsedMethodComponent then this exception is thrown.
     */
    public MethodCallGraphImpl(ParsedMethodComponent root) throws OrphanComponentException {
        this.root = root;
        this.sourceComponent = getSourceComponent(root);
        symbolResolver = sourceComponent.getSymbolResolver();
    }

    /**
     * it takes in ParsedMethodComponent and tries to traverse up the tree and
     * get SourceComponent and symbol resolver on it's own.
     *
     * @param root ParsedMethodComponent for which call graph needs to be generated.
     * @return SourceComponent of the tree of which ParsedMethodComponent is part of.
     * @throws OrphanComponentException If MethodCallGraph is unable to find parents
     *                                  of given ParsedMethodComponent then this exception is thrown.
     */
    private SourceComponent getSourceComponent(ParsedMethodComponent root) throws OrphanComponentException {
        if (root.getParent().isEmpty()) {
            throw new OrphanComponentException("Unable to get parent of this method, call graph cannot be generated.");
        }

        var parsedComponent = root.getParent().get();

        while (!parsedComponent.isSourceComponent()) {
            if (parsedComponent.getParent().isEmpty()) {
                throw new OrphanComponentException("Unable to get parent of this ParsedComponent, call graph cannot be generated.");
            }

            parsedComponent = parsedComponent.getParent().get();
        }

        if (parsedComponent.asSourceComponent().isEmpty()) {
            throw new OrphanComponentException("Unable to get SourceComponent, call graph cannot be generated.");
        }
        return parsedComponent.asSourceComponent().get();
    }

    /**
     * Get a concatenated representation of call graph.
     *
     * @return returns a representation of call graph in string form.
     */
    @Override
    public String getCallGraphString() {
        return null;
    }

    /**
     * Get a mapping from methodName (String) -> neighbors (List<String>)
     *
     * @return map from methodName to all its neighbors.
     */
    @Override
    public Map<String, List<String>> getCallGraphMap() {
        return null;
    }

    /**
     * Get a mapping from methodName (String) -> method (ParsedComponent)
     *
     * @return map from methodName to parsedComponent.
     */
    @Override
    public Map<String, ParsedMethodComponent> getAllMethods() {
        var rootMethodDeclaration = root.getAsResolvedMethodDeclaration()
                .orElseThrow(() -> new IllegalStateException("Parsed method component does not contain resolvedDeclaration."))
                .toAst()
                .orElseThrow(() -> new RuntimeException("unable to get AST from ResolvedMethodDeclaration."));


        Map<String, ParsedMethodComponent> methodComponentMap = new HashMap<>();

        rootMethodDeclaration
                .findAll(MethodCallExpr.class)
                .forEach(methodCallExpr -> {

                });
        return null;
    }
}
