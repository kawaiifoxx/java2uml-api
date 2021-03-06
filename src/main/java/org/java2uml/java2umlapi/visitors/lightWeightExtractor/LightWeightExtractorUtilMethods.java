package org.java2uml.java2umlapi.visitors.lightWeightExtractor;

import com.github.javaparser.resolution.declarations.ResolvedDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedParameterDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedTypeParameterDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import org.java2uml.java2umlapi.lightWeight.*;
import org.java2uml.java2umlapi.umlComponenets.ParsedClassOrInterfaceComponent;
import org.java2uml.java2umlapi.umlComponenets.ParsedComponent;
import org.java2uml.java2umlapi.umlComponenets.TypeRelation;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * A util class providing utility methods for LightWeightExtractor.
 * </p>
 *
 * @author kawaiifox
 */
public abstract class LightWeightExtractorUtilMethods {
    /**
     * Generates list of ClassRelation from set of typeRelation.
     *
     * @param classOrInterfaceMap         map of classOrInterfaceMap.
     * @param externalClassOrInterfaceMap map of externalClassOrInterfaceMap.
     * @param typeRelations               Set of relations b/w these class/interface.
     * @return list of ClassRelation.
     */
    static List<ClassRelation> getClassRelations(
            Map<String, ClassOrInterface> classOrInterfaceMap,
            Map<String, ClassOrInterface> externalClassOrInterfaceMap,
            Set<TypeRelation> typeRelations
    ) {
        return typeRelations
                .stream()
                .map(typeRelation -> {
                    var from = typeRelation.getFrom();
                    ClassOrInterface fromLW;
                    if (classOrInterfaceMap.containsKey(from.getName())) {
                        fromLW = classOrInterfaceMap.get(from.getName());
                    } else {
                        fromLW = externalClassOrInterfaceMap.get(from.getName());
                    }

                    var to = typeRelation.getTo();
                    ClassOrInterface toLW;
                    if (classOrInterfaceMap.containsKey(to.getName())) {
                        toLW = classOrInterfaceMap.get(to.getName());
                    } else {
                        toLW = externalClassOrInterfaceMap.get(to.getName());
                    }

                    return new ClassRelation(fromLW, toLW, typeRelation.getRelationsSymbol());
                })
                .collect(Collectors.toList());
    }

    /**
     * @param children children of sourceComponent may contain ParsedClassOrInterfaceComponent, ParsedEnumComponent
     * @return list of enumLW from children.
     */
    static List<EnumLW> getEnumLWList(Map<String, ParsedComponent> children, LightWeightExtractor lightWeightExtractor) {
        return children
                .values()
                .stream()
                .filter(ParsedComponent::isParsedEnumComponent)
                .map(ParsedComponent::asParsedEnumComponent)
                .map(Optional::get)
                .map(parsedEnumComponent -> parsedEnumComponent.accept(lightWeightExtractor))
                .map(LightWeight::asEnumLW)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * @param externalComponent map of external components containing classes out of project scope.
     * @return Map of ClassOrInterface.
     */
    static Map<String, ClassOrInterface> getExternalClassOrInterfaceList(
            Map<String, ParsedComponent> externalComponent, LightWeightExtractor lightWeightExtractor) {
        return externalComponent
                .values()
                .stream()
                .map(ParsedComponent::asParsedExternalComponent)
                .map(Optional::get)
                .map(parsedExternalComponent -> parsedExternalComponent.accept(lightWeightExtractor))
                .map(LightWeight::asClassOrInterface)
                .map(Optional::get)
                .collect(Collectors.toMap(ClassOrInterface::getName, classOrInterface -> classOrInterface));
    }

    /**
     * @param children map of classes/interfaces in project scope.
     * @return Map of ClassOrInterface
     */
    static Map<String, ClassOrInterface> getClassOrInterface(Map<String, ParsedComponent> children
            , LightWeightExtractor lightWeightExtractor) {
        return children
                .values()
                .stream()
                .filter(ParsedComponent::isParsedClassOrInterfaceComponent)
                .map(ParsedComponent::asParsedClassOrInterfaceComponent)
                .map(Optional::get)
                .map(parsedClassOrInterfaceComponent -> parsedClassOrInterfaceComponent.accept(lightWeightExtractor))
                .map(LightWeight::asClassOrInterface)
                .map(Optional::get)
                .collect(Collectors.toMap(ClassOrInterface::getName, classOrInterface -> classOrInterface));
    }

    /**
     * @param parsedClassOrInterfaceComponent component of which you want body of.
     * @return body of component.
     */
    static Body getBody(ParsedClassOrInterfaceComponent parsedClassOrInterfaceComponent) {
        var resolvedDeclaration =
                getResolvedReferenceTypeDeclaration(parsedClassOrInterfaceComponent);
        if (resolvedDeclaration.isClass() && resolvedDeclaration.asClass().toAst().isPresent()) {
            return new Body(resolvedDeclaration.asClass().toAst().get().toString());
        } else if (resolvedDeclaration.isInterface() && resolvedDeclaration.asInterface().toAst().isPresent()) {
            return new Body(resolvedDeclaration.asInterface().toAst().toString());
        }
        return new Body(resolvedDeclaration.getName() + " {}");
    }

    /**
     * @param parsedClassOrInterfaceComponent takes parsed class or interface
     * @return a resolvedReferenceTypeDeclaration
     */
    private static ResolvedReferenceTypeDeclaration getResolvedReferenceTypeDeclaration(
            ParsedClassOrInterfaceComponent parsedClassOrInterfaceComponent) {
        return getResolvedDeclaration(parsedClassOrInterfaceComponent)
                .asType()
                .asReferenceType();
    }

    /**
     * @param parsedComponent parsedComponent of which resolvedDeclaration you want to get.
     * @return ResolvedDeclaration of given parsedComponent.
     */
    static ResolvedDeclaration getResolvedDeclaration(ParsedComponent parsedComponent) {
        return parsedComponent
                .getResolvedDeclaration()
                .orElseThrow(() -> new IllegalStateException("parsedComponent should" +
                        " contain resolved declaration."));
    }

    /**
     * @param parsedClassOrInterfaceComponent component of which you want type parameters of.
     * @return a list of type parameters.
     */
    static List<TypeParam> getTypeParamList(ParsedClassOrInterfaceComponent parsedClassOrInterfaceComponent) {
        var resolvedDeclaration =
                getResolvedReferenceTypeDeclaration(parsedClassOrInterfaceComponent);

        if (!resolvedDeclaration.isGeneric()) {
            return new ArrayList<>();
        }

        return getTypeParametersFromRTPD(resolvedDeclaration.getTypeParameters());
    }

    /**
     * Get a list of TypeParameters from ResolvedTypeParameterDeclaration
     *
     * @param list list of ResolvedTypeParameterDeclaration
     * @return List of TypeParameters
     */
    static List<TypeParam> getTypeParametersFromRTPD(List<ResolvedTypeParameterDeclaration> list) {
        return list.stream()
                .map(rTPD -> new TypeParam(rTPD.getName()))
                .collect(Collectors.toList());
    }

    /**
     * converts all the ParsedMethodComponent to List of Method.
     *
     * @param children children from which Method will be extracted.
     * @return a list of Method.
     */
    static List<Method> getMethodList(Map<String, ParsedComponent> children, LightWeightExtractor lightWeightExtractor) {
        return children
                .values()
                .stream()
                .filter(ParsedComponent::isParsedMethodComponent)
                .map(ParsedComponent::asParsedMethodComponent)
                .map(Optional::get)
                .map(parsedMethodComponent -> parsedMethodComponent.accept(lightWeightExtractor))
                .map(LightWeight::asMethod)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * converts all the ParsedFieldComponent to List of Field.
     *
     * @param children children from which Field
     * @return a List of Field.
     */
    static List<Field> getFieldList(Map<String, ParsedComponent> children, LightWeightExtractor lightWeightExtractor) {
        return children
                .values()
                .stream()
                .filter(ParsedComponent::isParsedFieldComponent)
                .map(ParsedComponent::asParsedFieldComponent)
                .map(Optional::get)
                .map(parsedFieldComponent -> parsedFieldComponent.accept(lightWeightExtractor))
                .map(LightWeight::asField)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * converts all the ParsedConstructorComponent to  List of Constructor.
     *
     * @param children children from which Constructor will be children extracted.
     * @return a list of Constructor.
     */
    static List<Constructor> getConstructorList(Map<String, ParsedComponent> children, LightWeightExtractor lightWeightExtractor) {
        return children
                .values()
                .stream()
                .filter(ParsedComponent::isParsedConstructorComponent)
                .map(ParsedComponent::asParsedConstructorComponent)
                .map(Optional::get)
                .map(parsedConstructorComponent -> parsedConstructorComponent.accept(lightWeightExtractor))
                .map(LightWeight::asConstructor)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * @param children children of enum component.
     * @return a list of enumConstants.
     */
    static List<EnumConstant> getEnumConstantList(Map<String, ParsedComponent> children, LightWeightExtractor lightWeightExtractor) {
        return children
                .values()
                .stream()
                .filter(ParsedComponent::isParsedEnumConstantComponent)
                .map(ParsedComponent::asParsedEnumConstantComponent)
                .map(Optional::get)
                .map(parsedEnumConstantComponent -> parsedEnumConstantComponent.accept(lightWeightExtractor))
                .map(LightWeight::asEnumConstant)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * Get all the exceptions specified by a signature.
     *
     * @param exceptionList list of exception classes
     * @return list of SpecifiedException
     */
    static List<SpecifiedException> getSpecifiedExceptions(List<ResolvedType> exceptionList) {
        return exceptionList
                .stream()
                .filter(ResolvedType::isReferenceType)
                .map(ResolvedType::asReferenceType)
                .map(resolvedReferenceType -> new SpecifiedException(resolvedReferenceType.getQualifiedName()))
                .collect(Collectors.toList());
    }

    /**
     * Get a List of all the parameters specified by a signature
     *
     * @param numberOfParameters number of parameters in a signature
     * @param getParameter       Function with mapping i -> ResolvedParameterDeclaration, i.e get ith parameter from the
     *                           list of all the parameters.
     * @return List of all the parameters(Param).
     */
    static List<Param> getParamList(int numberOfParameters,
                                    Function<Integer, ResolvedParameterDeclaration> getParameter) {
        var parametersList = new ArrayList<Param>();

        for (int i = 0; i < numberOfParameters; i++) {
            ResolvedParameterDeclaration parameterDeclaration = getParameter.apply(i);
            parametersList.add(new Param(parameterDeclaration.describeType(), parameterDeclaration.getName()));
        }

        return parametersList;
    }

}
