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

    /**
     * Initializes sourceComponent and generates tree and all the type relations.
     *
     * @param allParsedTypes List of resolvedDeclarations
     */
    public SourceComponent(List<ResolvedDeclaration> allParsedTypes) {
        this.allParsedTypes = allParsedTypes;
        this.children = new HashMap<>();
        this.allRelations = new HashSet<>();
        this.externalComponents = new HashMap<>();

        for (var resolvedDeclaration : allParsedTypes) {
            if (resolvedDeclaration.asType().isEnum()) {
                ParsedEnumComponent parsedEnumComponent = new ParsedEnumComponent(resolvedDeclaration.asType().asEnum(), this);
                children.put(parsedEnumComponent.getName(), parsedEnumComponent);
                generateParsedEnumComponentFromResoldDecl(resolvedDeclaration, parsedEnumComponent);
            } else {
                ParsedComponent parsedClassOrInterfaceComponent = new ParsedClassOrInterfaceComponent(resolvedDeclaration, this);
                children.put(parsedClassOrInterfaceComponent.getName(), parsedClassOrInterfaceComponent);
                generateParsedClassOrInterfaceComponentFromResolvedDecl(resolvedDeclaration, parsedClassOrInterfaceComponent);
            }

        }

        children.forEach((k, v) -> {
            if (v.asParsedCompositeComponent().isPresent())
                generateTypeRelations(v.asParsedCompositeComponent().get());
        });

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

    /**
     * @return Returns all the resolvedDeclarations contained in the sourceComponent.
     */
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

    /**
     * Generates ParsedEnumComponent from ResolvedDeclaration and adds it to children.
     *
     * @param resolvedDeclaration ResolvedEnumDeclaration for generation of ParsedEnumComponent's children
     * @param parsedComponent     new ParsedEnumComponent to be generated.
     */
    private void generateParsedEnumComponentFromResoldDecl(ResolvedDeclaration resolvedDeclaration, ParsedEnumComponent parsedComponent) {
        if (resolvedDeclaration.isType() && resolvedDeclaration.asType().isEnum()) {
            var resolvedEnumDecl = resolvedDeclaration.asType().asEnum();
            var allEnumConstants = resolvedEnumDecl.getEnumConstants();

            allEnumConstants.forEach(resolvedEnumConstant -> parsedComponent
                    .addChild(new ParsedEnumConstantComponent(resolvedEnumConstant, parsedComponent)));

            var fieldList = resolvedEnumDecl.getDeclaredFields();

            fieldList.forEach(field -> parsedComponent
                    .addChild(new ParsedFieldComponent(parsedComponent, field)));

            var methodList = resolvedEnumDecl.getDeclaredMethods();

            methodList.forEach(method -> parsedComponent
                    .addChild(new ParsedMethodComponent(parsedComponent, method)));

            var constructorList = resolvedEnumDecl.getConstructors();

            constructorList.forEach(constructor -> parsedComponent
                    .addChild(new ParsedConstructorComponent(parsedComponent, constructor)));
        }

    }

    /**
     * Generates all the relations for the ParsedComponent from. This ParsedComponent should not be a leaf.
     *
     * @param from ParsedComponent from which relations need to be generated.
     */
    private void generateTypeRelations(ParsedCompositeComponent from) {
        if (from.isParsedClassOrInterfaceComponent()) {
            generateExtensionRelations(from);
            generateAggregationRelations(from);
            generateDependencyRelations(from);
        }
    }

    /**
     * Generates dependency relation for ParsedComponent from.
     *
     * @param from ParsedComponent from which dependency relation needs to be generated.
     */
    private void generateDependencyRelations(ParsedCompositeComponent from) {

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

                    if (to != null && to.asParsedCompositeComponent().isPresent())
                        allRelations.add(new TypeRelation(from, to.asParsedCompositeComponent().get(), DEPENDENCY_AR.toString()));
                }
            });
        });
    }

    /**
     * Generates aggregation relations for ParsedComponent from.
     *
     * @param from ParsedComponent from which relations need to be generated.
     */
    private void generateAggregationRelations(ParsedCompositeComponent from) {

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

                if (to != null && to.asParsedCompositeComponent().isPresent())
                    allRelations.add(new TypeRelation(from, to.asParsedCompositeComponent().get(), AGGREGATION.toString()));
            }
        });
    }

    /**
     * Generates extension relations from 'from'  to every ancestor of the 'from' component.
     *
     * @param from ParsedComponent from which extension relations need to be generated.
     */
    private void generateExtensionRelations(ParsedCompositeComponent from) {

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

            if (to != null && to.asParsedCompositeComponent().isPresent())
                allRelations.add(new TypeRelation(from, to.asParsedCompositeComponent().get(), UP + EXTENSION.toString()));
        });
    }

    /**
     * @return Returns Generated UML syntax.
     */
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

    /**
     * For each typeRelation in allRelations use .toUML() on it and appends a newline character, this generates
     * a single string containing UML of all the relations.
     */
    private void generateUMLTypeRelations() {
        StringBuilder generatedUMLTypesRelationsBuilder = new StringBuilder();

        allRelations.forEach(e ->
                generatedUMLTypesRelationsBuilder.append(e.toUML()).append("\n"));

        generatedUMLTypeRelations = generatedUMLTypesRelationsBuilder.toString();
    }

    /**
     * For each parsedComponent in children and externalComponent calls .toUML on it and appends them all to a string
     * with a newline character in between.
     */
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
                ", generatedUMLClasses='" + generatedUMLClasses + '\'' +
                ", generatedUMLTypeRelations='" + generatedUMLTypeRelations + '\'' +
                '}';
    }
}
