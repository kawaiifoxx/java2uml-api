package org.java2uml.java2umlapi.umlComponenets;

import com.github.javaparser.resolution.declarations.ResolvedDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedParameterDeclaration;
import org.java2uml.java2umlapi.util.umlSymbols.StartEnd;

import java.util.*;

import static org.java2uml.java2umlapi.util.umlSymbols.RelationsSymbol.*;
import static org.java2uml.java2umlapi.util.umlSymbols.RelationsSymbol.Direction.UP;

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
    private final Map<String, ParsedComponent> externalComponents;
    private final List<ResolvedDeclaration> allParsedTypes;
    private final Set<TypeRelation> allRelations;
    private String generatedUMLClasses;
    private String generatedUMLTypeRelations;

    public SourceComponent(List<ResolvedDeclaration> allParsedTypes) {
        this.allParsedTypes = allParsedTypes;
        this.children = new HashMap<>();
        this.allRelations = new HashSet<>();
        this.externalComponents = new HashMap<>();

        for (var resolvedDeclaration : allParsedTypes) {
            ParsedComponent parsedClassOrInterfaceComponent = new ParsedClassOrInterfaceComponent(resolvedDeclaration, this);
            children.put(parsedClassOrInterfaceComponent.getName(), parsedClassOrInterfaceComponent);
            generateParsedClassOrInterfaceComponentFromResolvedDecl(resolvedDeclaration, parsedClassOrInterfaceComponent);
        }

        children.forEach((k, v) -> generateTypeRelations(v));

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

        if ((resolvedDeclaration.asType().isInterface() || resolvedDeclaration.asType().isClass())
                && parsedComponent.isParsedClassOrInterfaceComponent()) {
            var typeDeclaration = resolvedDeclaration.asType().asReferenceType();
            //noinspection OptionalGetWithoutIsPresent
            var classOrInterfaceComponent = parsedComponent.asParsedClassOrInterfaceComponent().get();
            var fieldList = typeDeclaration.getDeclaredFields();

            fieldList.forEach(e -> classOrInterfaceComponent
                    .addChild(new ParsedFieldComponent(classOrInterfaceComponent, e)));

            Set<ResolvedMethodDeclaration> methodList = typeDeclaration.getDeclaredMethods();
            var constructorList = typeDeclaration.getConstructors();

            constructorList.forEach(e -> classOrInterfaceComponent
                    .addChild(new ParsedConstructorComponent(classOrInterfaceComponent, e)));

            methodList.forEach(e -> classOrInterfaceComponent
                    .addChild(new ParsedMethodComponent(classOrInterfaceComponent, e)));

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

        //noinspection OptionalGetWithoutIsPresent
        var resolvedTypeDeclaration = from.getResolvedDeclaration().get().asType();

        var dependencies = resolvedTypeDeclaration.asReferenceType().getDeclaredMethods();

        dependencies.forEach(dependency -> {
            List<ResolvedParameterDeclaration> parameterList = new ArrayList<>();

            for (int i = 0; i < dependency.getNumberOfParams(); i++) {
                parameterList.add(dependency.getParam(i));
            }

            parameterList.forEach(parameter -> {
                var typeOfParameter = parameter.getType();

                if (typeOfParameter.isReferenceType()) {
                    var to = children
                            .get(typeOfParameter
                                    .asReferenceType()
                                    .getQualifiedName());

                    if (to != null)
                        allRelations.add(new TypeRelation(from, to, DEPENDENCY_AR.toString()));
                }
            });
        });
    }

    private void generateAggregationRelations(ParsedComponent from) {

        //noinspection OptionalGetWithoutIsPresent
        var resolvedTypeDeclaration = from.getResolvedDeclaration().get().asType();

        var aggregations = resolvedTypeDeclaration.asReferenceType().getDeclaredFields();

        aggregations.forEach(aggregation -> {
            var declaringType = aggregation.getType();
            if (declaringType.isReferenceType()) {
                var declaringReferenceType = declaringType.asReferenceType();
                var to = children
                        .get(declaringReferenceType
                                .getQualifiedName());

                if (to == null && !declaringReferenceType.getQualifiedName().startsWith("java.lang")) {
                    if (declaringReferenceType.getTypeDeclaration().isPresent()) {
                        to = new ParsedExternalComponent(declaringReferenceType.getTypeDeclaration().get());
                        externalComponents.put(to.getName(), to);
                    }
                }

                if (to != null)
                    allRelations.add(new TypeRelation(from, to, AGGREGATION.toString()));
            }
        });
    }

    private void generateExtensionRelations(ParsedComponent from) {

        //noinspection OptionalGetWithoutIsPresent
        var resolvedTypeDeclaration = from.getResolvedDeclaration().get().asType();

        var ancestors = resolvedTypeDeclaration.asReferenceType().getAncestors();

        ancestors.forEach(ancestor -> {

            String ancestorName = ancestor.getQualifiedName();
            var isInChildren = children.containsKey(ancestorName);
            var isInChildrenOrInExternal = isInChildren
                    || externalComponents.containsKey(ancestorName);

            if (!isInChildrenOrInExternal && !ancestorName.startsWith("java.lang.Object")
                    && ancestor.getTypeDeclaration().isPresent()) {
                externalComponents.put(ancestorName, new ParsedExternalComponent(ancestor.getTypeDeclaration().get()));
            }

            ParsedComponent to;
            if (isInChildren) {
                to = children.get(ancestorName);
            } else {
                to = externalComponents.get(ancestorName);
            }

            if (to != null)
                allRelations.add(new TypeRelation(from, to, UP + EXTENSION.toString()));
        });
    }

    @Override
    public String toUML() {
        if (generatedUMLClasses == null)
            generateUMLClasses();

        if (generatedUMLTypeRelations == null) {
            generateUMLTypeRelations();
        }

        return StartEnd.START.toString()
                + "\n" + generatedUMLClasses
                + "\n" + generatedUMLTypeRelations
                + "\n" + StartEnd.END;
    }

    private void generateUMLTypeRelations() {
        StringBuilder generatedUMLTypesRelationsBuilder = new StringBuilder();

        allRelations.forEach(e ->
                generatedUMLTypesRelationsBuilder.append(e.toUML()).append("\n"));

        generatedUMLTypeRelations = generatedUMLTypesRelationsBuilder.toString();
    }

    private void generateUMLClasses() {
        StringBuilder generatedUMLClassesBuilder = new StringBuilder();

        children.forEach((k, v) ->
                generatedUMLClassesBuilder.append(v.toUML()).append("\n"));
        externalComponents.forEach((k, v) ->
                generatedUMLClassesBuilder.append(v.toUML()).append("\n"));

        generatedUMLClasses = generatedUMLClassesBuilder.toString();
    }

    @Override
    public String toString() {
        return "SourceComponent{" +
                ", genratedUMLClasses='" + generatedUMLClasses + '\'' +
                ", genratedUMLTypeRelations='" + generatedUMLTypeRelations + '\'' +
                '}';
    }
}
