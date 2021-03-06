package org.java2uml.java2umlapi.visitors.lightWeightExtractor;

import org.java2uml.java2umlapi.lightWeight.*;
import org.java2uml.java2umlapi.umlComponenets.*;
import org.java2uml.java2umlapi.visitors.Visitor;

import java.util.ArrayList;

import static org.java2uml.java2umlapi.visitors.lightWeightExtractor.LightWeightExtractorUtilMethods.*;

/**
 * <p>
 * This visitor can be used for extracting out representational information for each component.
 * As the name suggests, this visitor extracts LightWeight entities from ParsedComponents.
 * </p>
 *
 * @author kawaiifox
 */
public class LightWeightExtractor implements Visitor<LightWeight> {

    /**
     * Visits passed component and extracts lightWeight from it.
     *
     * @param sourceComponent sourceComponent representing project.
     * @return Source a light weight representation of the project.
     */
    @Override
    public LightWeight visit(SourceComponent sourceComponent) {
        var children = sourceComponent.getChildren();
        var classOrInterfaceMap = getClassOrInterface(children, this);
        var externalClassOrInterfaceMap = getExternalClassOrInterfaceList(
                sourceComponent.getExternalComponents(), this);
        var typeRelations = sourceComponent.getAllRelations();
        var classRelationList = getClassRelations(
                classOrInterfaceMap, externalClassOrInterfaceMap, typeRelations);
        var classOrInterfaceList = new ArrayList<>(classOrInterfaceMap.values());
        classOrInterfaceList.addAll(externalClassOrInterfaceMap.values());
        return new Source(classOrInterfaceList, getEnumLWList(children, this), classRelationList);
    }

    /**
     * Visits passed component and extracts lightWeight from it.
     *
     * @param parsedClassOrInterfaceComponent parsedClassOrInterfaceComponent representing ClassOrInterface.
     * @return Object of type R
     */
    @Override
    public LightWeight visit(ParsedClassOrInterfaceComponent parsedClassOrInterfaceComponent) {
        var children = parsedClassOrInterfaceComponent.getChildren();
        return new ClassOrInterface(parsedClassOrInterfaceComponent.getName(),
                parsedClassOrInterfaceComponent.isClass(),
                false,
                getConstructorList(children, this),
                getMethodList(children, this),
                getFieldList(children, this),
                getTypeParamList(parsedClassOrInterfaceComponent),
                getBody(parsedClassOrInterfaceComponent)
        );
    }

    /**
     * Visits passed component and extracts lightWeight from it.
     *
     * @param parsedExternalComponent parsedExternalComponent representing External Class or interface or enum.
     * @return Object of type R
     */
    @Override
    public LightWeight visit(ParsedExternalComponent parsedExternalComponent) {
        var resolvedDeclaration = getResolvedDeclaration(parsedExternalComponent)
                .asType().asReferenceType();
        return new ClassOrInterface(
                parsedExternalComponent.getName(),
                resolvedDeclaration.isClass(),
                true,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                getTypeParametersFromRTPD(resolvedDeclaration.getTypeParameters()),
                new Body(resolvedDeclaration.getQualifiedName() + " {\n}")
        );
    }

    /**
     * Visits passed component and extracts lightWeight from it.
     *
     * @param parsedEnumComponent parsedEnumComponent representing Enum.
     * @return Object of type R
     */
    @Override
    public LightWeight visit(ParsedEnumComponent parsedEnumComponent) {
        var children = parsedEnumComponent.getChildren();
        return new EnumLW(
                parsedEnumComponent.getName(),
                getEnumConstantList(children, this),
                getConstructorList(children, this),
                getMethodList(children, this),
                getFieldList(children, this),
                new Body(parsedEnumComponent.getName() + " {}")
        );
    }

    /**
     * Visits passed component and extracts lightWeight from it.
     *
     * @param parsedMethodComponent parsedMethodComponent representing Method.
     * @return Object of type R
     */
    @Override
    public LightWeight visit(ParsedMethodComponent parsedMethodComponent) {
        var resolvedMethodDeclaration =
                parsedMethodComponent
                        .getAsResolvedMethodDeclaration()
                        .orElseThrow(() -> new IllegalStateException("parsedMethodComponent should" +
                                " contain resolvedMethodDeclaration."));

        return new Method(
                parsedMethodComponent.getName(),
                parsedMethodComponent.getReturnTypeName(),
                resolvedMethodDeclaration.getSignature(),
                resolvedMethodDeclaration.accessSpecifier().asString(),
                getParamList(resolvedMethodDeclaration.getNumberOfParams(), resolvedMethodDeclaration::getParam),
                getTypeParametersFromRTPD(resolvedMethodDeclaration.getTypeParameters()),
                getSpecifiedExceptions(resolvedMethodDeclaration.getSpecifiedExceptions()),
                new Body(resolvedMethodDeclaration.toAst().toString())
        );
    }

    /**
     * Visits passed component and extracts lightWeight from it.
     *
     * @param parsedConstructorComponent parsedConstructorComponent representing Constructor.
     * @return Object of type R
     */
    @Override
    public LightWeight visit(ParsedConstructorComponent parsedConstructorComponent) {
        var resolvedDeclaration =
                parsedConstructorComponent.getResolvedConstructorDeclaration();

        return new Constructor(
                parsedConstructorComponent.getName(),
                resolvedDeclaration.getSignature(),
                resolvedDeclaration.accessSpecifier().asString(),
                getParamList(resolvedDeclaration.getNumberOfParams(), resolvedDeclaration::getParam),
                getTypeParametersFromRTPD(resolvedDeclaration.getTypeParameters()),
                new Body(resolvedDeclaration.toAst().toString())
        );
    }

    /**
     * Visits passed component and extracts lightWeight from it.
     *
     * @param parsedFieldComponent parsedFieldComponent representing Field.
     * @return Object of type R
     */
    @Override
    public LightWeight visit(ParsedFieldComponent parsedFieldComponent) {
        var resolvedDeclaration = parsedFieldComponent.getResolvedFieldDeclaration();
        return new Field(
                resolvedDeclaration.getType().describe(),
                resolvedDeclaration.getName(),
                resolvedDeclaration.accessSpecifier().asString()
        );
    }

    /**
     * Visits passed component and extracts lightWeight from it.
     *
     * @param parsedEnumConstantComponent parsedEnumConstantComponent representing Enum constant.
     * @return Object of type R
     */
    @Override
    public LightWeight visit(ParsedEnumConstantComponent parsedEnumConstantComponent) {
        return new EnumConstant(parsedEnumConstantComponent.getName());
    }
}
