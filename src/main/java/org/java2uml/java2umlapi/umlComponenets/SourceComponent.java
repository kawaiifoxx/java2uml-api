package org.java2uml.java2umlapi.umlComponenets;

import com.github.javaparser.resolution.declarations.ResolvedDeclaration;

import java.util.*;

/**
 * <p>
 * A composite component representing whole java src code, this pattern is meant to be the
 * root of the whole tree.
 * </p>
 *
 * @author kawaiifox
 */
public class SourceComponent implements ParsedComponent {

    private Map<String,ParsedComponent> children;
    private final List<ResolvedDeclaration> allParsedTypes;
    private Set<TypeRelation> allRelations;

    public SourceComponent(List<ResolvedDeclaration> allParsedTypes) {
        this.allParsedTypes = allParsedTypes;
        this.children = new HashMap<>();
        this.allRelations = new HashSet<>();

        //TODO: FINISH THIS!
        for (var resolvedDeclaration : allParsedTypes) {

        }
    }

    @Override
    public boolean isSourceComponent() {
        return true;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public Optional<SourceComponent> asSourceComponent() {
        return Optional.of(this);
    }

    public List<ResolvedDeclaration> getAllParsedTypes() {
        return allParsedTypes;
    }

    @Override
    public Optional<ParsedComponent> getParent() {
        return Optional.empty();
    }

    @Override
    public Optional<Map<String, ParsedComponent>> getChildren() {
        if (children == null)
            return Optional.empty();
        return Optional.of(children);
    }

    @Override
    public String getName() {
        return "SourceComponent";
    }

    /**
     * Generates ParsedClassOrInterfaceComponent from ResolvedDeclaration and adds it children.
     *
     * @param resolvedDeclaration ResolvedDeclaration to be converted to ParsedClassOrInterfaceComponent
     * @param parsedComponent     new ParsedClassOrInterfaceComponent to be generated.
     */
    private void generateParsedClassOrInterfaceComponentFromResolvedDecl(ResolvedDeclaration resolvedDeclaration,
                                                                         ParsedComponent parsedComponent) {

        if (resolvedDeclaration.isType() && parsedComponent.isParsedClassOrInterfaceComponent()) {
            var typeDeclaration = resolvedDeclaration.asType().asReferenceType();
            var classOrInterfaceComponent = parsedComponent.asParsedClassOrInterfaceComponent().get();
            var fieldList = typeDeclaration.getDeclaredFields();

            fieldList.forEach(e ->
                    classOrInterfaceComponent
                            .addChild(new ParsedFieldComponent(classOrInterfaceComponent, e)));

            var methodList = typeDeclaration.getDeclaredMethods();

            methodList.forEach(e -> {
                if (e.getReturnType().isNull())
                    classOrInterfaceComponent
                            .addChild(new ParsedConstructorComponent(classOrInterfaceComponent, e));
                else
                    classOrInterfaceComponent
                            .addChild(new ParsedMethodComponent(classOrInterfaceComponent, e));
            });

        }
    }

    @Override
    public String toString() {
        return "to be implemented";
    }
}
