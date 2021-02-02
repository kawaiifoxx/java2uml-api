package org.java2uml.java2umlapi.umlComponenets;

import com.github.javaparser.resolution.declarations.ResolvedDeclaration;

import java.util.*;

import static org.java2uml.java2umlapi.util.umlSymbols.RelationsSymbol.*;

/**
 * <p>
 * A composite component representing whole java src code, this pattern is meant to be the
 * root of the whole tree.
 * </p>
 *
 * @author kawaiifox
 */
public class SourceComponent implements ParsedComponent {

    private final Map<String, ParsedComponent> children;
    private final List<ResolvedDeclaration> allParsedTypes;
    private final Set<TypeRelation> allRelations;

    public SourceComponent(List<ResolvedDeclaration> allParsedTypes) {
        this.allParsedTypes = allParsedTypes;
        this.children = new HashMap<>();
        this.allRelations = new HashSet<>();

        for (var resolvedDeclaration : allParsedTypes) {
            ParsedComponent parsedClassOrInterfaceComponent = new ParsedClassOrInterfaceComponent(resolvedDeclaration, this);
            children.put(parsedClassOrInterfaceComponent.getName(), parsedClassOrInterfaceComponent);
            generateParsedClassOrInterfaceComponentFromResolvedDecl(resolvedDeclaration, parsedClassOrInterfaceComponent);
        }

        children.forEach((k,v) -> generateTypeRelations(v));

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
            //noinspection OptionalGetWithoutIsPresent
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

    private void generateTypeRelations(ParsedComponent from) {
        if (from.isParsedClassOrInterfaceComponent()) {
            generateExtensionRelations(from);
            generateAggregationRelations(from);
            generateDependencyRelations(from);
        }
    }

    private void generateDependencyRelations(ParsedComponent from) {

        @SuppressWarnings("OptionalGetWithoutIsPresent") var resolvedTypeDeclaration = from.getResolvedDeclaration().get().asType();

        var dependencies = resolvedTypeDeclaration.asReferenceType().getDeclaredMethods();

        dependencies.forEach(dependency -> {
            var parameterList = dependency.getTypeParameters();

            parameterList.forEach(parameter -> {
                var typeOfParameter = parameter.asParameter().getType();

                if (typeOfParameter.isReferenceType()) {
                    var to = children
                            .get(typeOfParameter
                                    .asReferenceType()
                                    .getQualifiedName());

                    if (to != null)
                        allRelations.add(new TypeRelation(from, to, DEPENDENCY_AR));
                }
            });
        });
    }

    private void generateAggregationRelations(ParsedComponent from) {

        //noinspection OptionalGetWithoutIsPresent
        var resolvedTypeDeclaration = from.getResolvedDeclaration().get().asType();

        var aggregations = resolvedTypeDeclaration.asReferenceType().getDeclaredFields();

        aggregations.forEach(aggregation -> {
            var declaringType = aggregation.declaringType().asReferenceType();
            if (declaringType.isInterface() || declaringType.isClass() || declaringType.isGeneric()) {
                var to = children
                        .get(declaringType
                                .getQualifiedName());
                allRelations.add(new TypeRelation(from, to, AGGREGATION));
            }
        });
    }

    private void generateExtensionRelations(ParsedComponent from) {

        //noinspection OptionalGetWithoutIsPresent
        var resolvedTypeDeclaration = from.getResolvedDeclaration().get().asType();

        var ancestors = resolvedTypeDeclaration.asReferenceType().getAncestors();

        ancestors.forEach(ancestor -> {
            var to = children
                    .get(ancestor
                            .getQualifiedName());

            allRelations.add(new TypeRelation(from, to, EXTENSION));
        });
    }

    @Override
    public String toString() {
        return "to be implemented";
    }
}
