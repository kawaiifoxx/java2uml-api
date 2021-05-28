package org.java2uml.java2umlapi.visitors.lightWeightExtractor;

import org.java2uml.java2umlapi.lightWeight.*;
import org.java2uml.java2umlapi.parsedComponent.*;
import org.java2uml.java2umlapi.visitors.Visitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    Logger logger = LoggerFactory.getLogger(LightWeightExtractor.class);


    /**
     * Visits passed component and extracts lightWeight from it.
     *
     * @param sourceComponent sourceComponent representing project.
     * @return Source a light weight representation of the project.
     */
    @Override
    public LightWeight visit(SourceComponent sourceComponent) {
        var children = sourceComponent.getChildren();
        var typeRelations = sourceComponent.getAllRelations();

        var source = new Source();
        var externalClassOrInterfaceMap = getExternalClassOrInterfaceList(
                sourceComponent.getExternalComponents(),
                this,
                source
        );
        var classOrInterfaceMap = getClassOrInterface(
                children,
                this,
                source
        );
        var classOrInterfaceList = new ArrayList<>(classOrInterfaceMap.values());
        classOrInterfaceList.addAll(externalClassOrInterfaceMap.values());
        source.setClassOrInterfaceList(classOrInterfaceList);
        source.setClassRelationList(
                getClassRelations(
                        classOrInterfaceMap,
                        externalClassOrInterfaceMap,
                        typeRelations,
                        source
                )
        );
        source.setEnumLWList(getEnumLWList(children, this, source));

        return source;
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
        var classOrInterface = new ClassOrInterface.Builder()
                .withName(parsedClassOrInterfaceComponent.getName())
                .withPackageName(parsedClassOrInterfaceComponent.getPackageName())
                .withIsClass(parsedClassOrInterfaceComponent.isClass())
                .withIsExternal(false)
                .build();

        classOrInterface.setPackageName(parsedClassOrInterfaceComponent.getPackageName());
        classOrInterface.setClassConstructors(getConstructorList(children, this, classOrInterface));
        classOrInterface.setClassOrInterfaceMethods(
                getMethodList(
                        children,
                        this,
                        classOrInterface,
                        parsedClassOrInterfaceComponent.getPackageName()
                )
        );
        classOrInterface.setClassFields(getFieldList(children, this, classOrInterface));
        classOrInterface.setClassOrInterfaceTypeParameters(
                getTypeParamList(parsedClassOrInterfaceComponent, classOrInterface)
        );
        classOrInterface.setBody(getBody(parsedClassOrInterfaceComponent, classOrInterface));

        return classOrInterface;
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
        var classOrInterface = new ClassOrInterface.Builder()
                .withName(parsedExternalComponent.getName())
                .withPackageName(parsedExternalComponent.getPackageName())
                .withIsClass(resolvedDeclaration.isClass())
                .withIsExternal(true)
                .build();

        classOrInterface.setClassConstructors(new ArrayList<>());
        classOrInterface.setClassOrInterfaceMethods(new ArrayList<>());
        classOrInterface.setClassFields(new ArrayList<>());
        classOrInterface.setClassOrInterfaceTypeParameters(
                getTypeParametersFromRTPD(resolvedDeclaration.getTypeParameters(), classOrInterface)
        );
        classOrInterface.setBody(new Body(resolvedDeclaration.getQualifiedName() + " {\n}"));
        return classOrInterface;
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
        var enumLW = new EnumLW(parsedEnumComponent.getName());
        enumLW.setPackageName(parsedEnumComponent.getPackageName());
        enumLW.setEnumConstants(getEnumConstantList(children, this, enumLW));
        enumLW.setEnumConstructors(getConstructorList(children, this, enumLW));
        enumLW.setEnumMethods(
                getMethodList(children, this, enumLW, parsedEnumComponent.getPackageName())
        );
        enumLW.setEnumFields(getFieldList(children, this, enumLW));
        enumLW.setBody(new Body(parsedEnumComponent.getName() + "{\n}", enumLW));
        return enumLW;
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
        var method = new Method.Builder()
                .withName(parsedMethodComponent.getName())
                .withSignature(resolvedMethodDeclaration.getQualifiedSignature())
                .withReturnType(parsedMethodComponent.getReturnTypeName())
                .withVisibility(resolvedMethodDeclaration.accessSpecifier().asString())
                .withStatic(resolvedMethodDeclaration.isStatic())
                .build();

        method.setMethodParameters(
                getParamList(resolvedMethodDeclaration.getNumberOfParams(), resolvedMethodDeclaration::getParam, method)
        );
        method.setMethodTypeParameters(
                getTypeParametersFromRTPD(resolvedMethodDeclaration.getTypeParameters(), method)
        );
        method.setSpecifiedExceptions(
                getSpecifiedExceptions(resolvedMethodDeclaration.getSpecifiedExceptions(), method)
        );
        try {
            method.setBody(
                    new Body(resolvedMethodDeclaration.toAst()
                            .orElseThrow(
                                    () -> new RuntimeException("unable to get ast of method," +
                                            " body cannot be generated")
                            ).toString(),
                            method
                    )
            );
        } catch (RuntimeException exception) {
            logger.info("Unable to generate body for method", exception);
        }

        return method;
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

        var constructor = new Constructor(
                parsedConstructorComponent.getName(),
                resolvedDeclaration.getSignature(),
                resolvedDeclaration.accessSpecifier().asString(),
                resolvedDeclaration.toAst().isEmpty()
        );
        constructor.setConstructorParameters(
                getParamList(resolvedDeclaration.getNumberOfParams(), resolvedDeclaration::getParam, constructor)
        );
        constructor.setConstructorTypeParameters(
                getTypeParametersFromRTPD(resolvedDeclaration.getTypeParameters(), constructor)
        );
        constructor.setConstructorSpecifiedExceptions(
                getSpecifiedExceptions(resolvedDeclaration.getSpecifiedExceptions(), constructor)
        );
        constructor.setBody(getBody(resolvedDeclaration, constructor));
        return constructor;
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
                parsedFieldComponent.getTypeName(),
                resolvedDeclaration.getName(),
                resolvedDeclaration.accessSpecifier().asString(),
                resolvedDeclaration.isStatic()
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

    /**
     * Visits passed TypeRelation and performs some operation on it.
     * <br>
     * Please note that, this method always returns null because it has not been
     * implemented.
     *
     * @param typeRelation typeRelation on  which you want to perform the operation.
     */
    @Override
    @Deprecated
    public LightWeight visit(TypeRelation typeRelation) {
        return null;
    }
}
