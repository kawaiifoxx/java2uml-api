package org.java2uml.java2umlapi.visitors.lightWeightExtractor;

import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.*;
import com.github.javaparser.resolution.types.ResolvedType;
import org.java2uml.java2umlapi.lightWeight.*;
import org.java2uml.java2umlapi.parsedComponent.ParsedClassOrInterfaceComponent;
import org.java2uml.java2umlapi.parsedComponent.ParsedComponent;
import org.java2uml.java2umlapi.parsedComponent.TypeRelation;

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
     * @param lightWeightMap              map of lightWeightMap.
     * @param typeRelations               Set of relations b/w these class/interface.
     * @return list of ClassRelation.
     */
    static List<ClassRelation> getClassRelations(
            Map<String, LightWeight> lightWeightMap,
            Set<TypeRelation> typeRelations,
            Source source
    ) {
        return typeRelations
                .stream()
                .map(typeRelation -> {
                    var from = typeRelation.getFrom();
                    var to = typeRelation.getTo();
                    return new ClassRelation(
                            lightWeightMap.get(from.getName()),
                            lightWeightMap.get(to.getName()),
                            typeRelation.getRelationsSymbol(), source);
                }).collect(Collectors.toList());
    }

    /**
     * @param children children of sourceComponent may contain ParsedClassOrInterfaceComponent, ParsedEnumComponent
     * @param parent   parent to be injected into all enumLWs.
     * @return list of enumLW from children.
     */
    static List<EnumLW> getEnumLWList(
            Map<String, ParsedComponent> children, LightWeightExtractor lightWeightExtractor, Source parent
    ) {
        return children
                .values()
                .stream()
                .filter(ParsedComponent::isParsedEnumComponent)
                .map(ParsedComponent::asParsedEnumComponent)
                .map(Optional::get)
                .map(parsedEnumComponent -> parsedEnumComponent.accept(lightWeightExtractor))
                .map(LightWeight::asEnumLW)
                .map(Optional::get)
                .peek(enumLW -> enumLW.setParent(parent))
                .collect(Collectors.toList());
    }

    /**
     * @param externalComponent map of external components containing classes out of project scope.
     * @param parent            parent to be injected into all external classes.
     * @return Map of ClassOrInterface.
     */
    static List<ClassOrInterface> getExternalClassOrInterfaceList(
            Map<String, ParsedComponent> externalComponent,
            LightWeightExtractor lightWeightExtractor,
            Source parent
    ) {
        return externalComponent
                .values()
                .stream()
                .map(ParsedComponent::asParsedExternalComponent)
                .map(Optional::get)
                .map(parsedExternalComponent -> parsedExternalComponent.accept(lightWeightExtractor))
                .map(LightWeight::asClassOrInterface)
                .map(Optional::get)
                .peek(classOrInterface -> classOrInterface.setParent(parent))
                .collect(Collectors.toList());
    }

    /**
     * @param children map of classes/interfaces in project scope.
     * @return List of ClassOrInterface
     */
    static List<ClassOrInterface> getClassOrInterface(
            Map<String, ParsedComponent> children,
            LightWeightExtractor lightWeightExtractor,
            Source source
    ) {
        return children
                .values()
                .stream()
                .filter(ParsedComponent::isParsedClassOrInterfaceComponent)
                .map(ParsedComponent::asParsedClassOrInterfaceComponent)
                .map(Optional::get)
                .map(parsedClassOrInterfaceComponent -> parsedClassOrInterfaceComponent.accept(lightWeightExtractor))
                .map(LightWeight::asClassOrInterface)
                .map(Optional::get)
                .peek(classOrInterface -> classOrInterface.setParent(source))
                .collect(Collectors.toList());
    }

    /**
     * @param parsedClassOrInterfaceComponent component of which you want body of.
     * @param parent                          parent to injected into the body.
     * @return body of component.
     */
    static Body getBody(ParsedClassOrInterfaceComponent parsedClassOrInterfaceComponent, LightWeight parent) {
        var resolvedDeclaration =
                getResolvedReferenceTypeDeclaration(parsedClassOrInterfaceComponent);
        if (resolvedDeclaration.isClass() && resolvedDeclaration.asClass().toAst().isPresent()) {
            return new Body(resolvedDeclaration.asClass().toAst().get().toString(), parent);
        } else if (resolvedDeclaration.isInterface() && resolvedDeclaration.asInterface().toAst().isPresent()) {
            return new Body(resolvedDeclaration.asInterface().toAst().get().toString(), parent);
        }
        return new Body(resolvedDeclaration.getName() + " {}", parent);
    }

    /**
     * @param resolvedDeclaration ResolvedConstructorDeclaration of which you want body of.
     * @param parent              parent to be injected in body.
     * @return body of parent.
     */
    static Body getBody(ResolvedConstructorDeclaration resolvedDeclaration, Constructor parent) {
        Body body;
        if (resolvedDeclaration.toAst().isPresent()) {
            body = new Body(resolvedDeclaration.toAst().get().toString(), parent);
        } else {
            body = new Body(resolvedDeclaration.getSignature() + " {\n}", parent);
        }
        return body;
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
     * @param parent                          parent to be injected in all the type parameters.
     * @return a list of type parameters.
     */
    static List<TypeParam> getTypeParamList(
            ParsedClassOrInterfaceComponent parsedClassOrInterfaceComponent, LightWeight parent
    ) {
        var resolvedDeclaration =
                getResolvedReferenceTypeDeclaration(parsedClassOrInterfaceComponent);

        if (!resolvedDeclaration.isGeneric()) {
            return new ArrayList<>();
        }

        return getTypeParametersFromRTPD(resolvedDeclaration.getTypeParameters(), parent);
    }

    /**
     * Get a list of TypeParameters from ResolvedTypeParameterDeclaration
     *
     * @param list   list of ResolvedTypeParameterDeclaration
     * @param parent parent to be injected in all the type parameters.
     * @return List of TypeParameters
     */
    static List<TypeParam> getTypeParametersFromRTPD(List<ResolvedTypeParameterDeclaration> list, LightWeight parent) {
        return list.stream()
                .map(rTPD -> new TypeParam(rTPD.getName()))
                .peek(typeParam -> typeParam.setParent(parent))
                .collect(Collectors.toList());
    }

    /**
     * converts all the ParsedMethodComponent to List of Method.
     *
     * @param children children from which Method will be extracted.
     * @param parent   parent to be injected in all the methods.
     * @return a list of Method.
     */
    static List<Method> getMethodList(
            Map<String, ParsedComponent> children,
            LightWeightExtractor lightWeightExtractor,
            LightWeight parent,
            String packageName
    ) {
        return children
                .values()
                .stream()
                .filter(ParsedComponent::isParsedMethodComponent)
                .map(ParsedComponent::asParsedMethodComponent)
                .map(Optional::get)
                .map(parsedMethodComponent -> parsedMethodComponent.accept(lightWeightExtractor))
                .map(LightWeight::asMethod)
                .map(Optional::get)
                .peek(method -> method.setParent(parent))
                .peek(method -> method.setPackageName(packageName))
                .collect(Collectors.toList());
    }

    /**
     * converts all the ParsedFieldComponent to List of Field.
     *
     * @param children children from which Field
     * @param parent   parent to be injected into fields.
     * @return a List of Field.
     */
    static List<Field> getFieldList(
            Map<String, ParsedComponent> children,
            LightWeightExtractor lightWeightExtractor,
            LightWeight parent
    ) {
        return children
                .values()
                .stream()
                .filter(ParsedComponent::isParsedFieldComponent)
                .map(ParsedComponent::asParsedFieldComponent)
                .map(Optional::get)
                .map(parsedFieldComponent -> parsedFieldComponent.accept(lightWeightExtractor))
                .map(LightWeight::asField)
                .map(Optional::get)
                .peek(field -> field.setParent(parent))
                .collect(Collectors.toList());
    }

    /**
     * converts all the ParsedConstructorComponent to  List of Constructor.
     *
     * @param children children from which Constructor will be children extracted.
     * @param parent   class or interface to be injected as the parent of constructor.
     * @return a list of Constructor.
     */
    static List<Constructor> getConstructorList(
            Map<String, ParsedComponent> children,
            LightWeightExtractor lightWeightExtractor,
            LightWeight parent
    ) {
        return children
                .values()
                .stream()
                .filter(ParsedComponent::isParsedConstructorComponent)
                .map(ParsedComponent::asParsedConstructorComponent)
                .map(Optional::get)
                .map(parsedConstructorComponent -> parsedConstructorComponent.accept(lightWeightExtractor))
                .map(LightWeight::asConstructor)
                .map(Optional::get)
                .peek(constructor -> constructor.setParent(parent))
                .collect(Collectors.toList());
    }

    /**
     * @param children children of enum component.
     * @param parent   parent to be injected in enum constants.
     * @return a list of enumConstants.
     */
    static List<EnumConstant> getEnumConstantList(Map<String, ParsedComponent> children, LightWeightExtractor lightWeightExtractor, LightWeight parent) {
        return children
                .values()
                .stream()
                .filter(ParsedComponent::isParsedEnumConstantComponent)
                .map(ParsedComponent::asParsedEnumConstantComponent)
                .map(Optional::get)
                .map(parsedEnumConstantComponent -> parsedEnumConstantComponent.accept(lightWeightExtractor))
                .map(LightWeight::asEnumConstant)
                .map(Optional::get)
                .peek(enumConstant -> enumConstant.setParent(parent))
                .collect(Collectors.toList());
    }

    /**
     * Get all the exceptions specified by a signature.
     *
     * @param exceptionList list of exception classes
     * @param parent        parent to be injected in type parameters.
     * @return list of SpecifiedException
     */
    static List<SpecifiedException> getSpecifiedExceptions(List<ResolvedType> exceptionList, LightWeight parent) {
        return exceptionList
                .stream()
                .filter(ResolvedType::isReferenceType)
                .map(ResolvedType::asReferenceType)
                .map(resolvedReferenceType -> new SpecifiedException(resolvedReferenceType.getQualifiedName(), parent))
                .collect(Collectors.toList());
    }

    /**
     * Get a List of all the parameters specified by a signature
     *
     * @param numberOfParameters number of parameters in a signature
     * @param getParameter       Function with mapping i -> ResolvedParameterDeclaration, i.e get ith parameter from the
     *                           list of all the parameters.
     * @param parent             parent to be injected in each parameter.
     * @return List of all the parameters(Param).
     */
    static List<Param> getParamList(int numberOfParameters,
                                    Function<Integer, ResolvedParameterDeclaration> getParameter, LightWeight parent) {
        var parametersList = new ArrayList<Param>();

        for (int i = 0; i < numberOfParameters; i++) {
            ResolvedParameterDeclaration parameterDeclaration = getParameter.apply(i);
            String paramType;
            try {
                paramType = parameterDeclaration.describeType();
            } catch (UnsolvedSymbolException e) {
                paramType = e.getName();
            }

            parametersList.add(new Param(paramType, parameterDeclaration.getName(), parent));
        }

        return parametersList;
    }

}
