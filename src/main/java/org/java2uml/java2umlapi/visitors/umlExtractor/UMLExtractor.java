package org.java2uml.java2umlapi.visitors.umlExtractor;

import org.java2uml.java2umlapi.parsedComponent.*;
import org.java2uml.java2umlapi.util.umlSymbols.StartEnd;
import org.java2uml.java2umlapi.util.umlSymbols.UMLModifier;
import org.java2uml.java2umlapi.util.umlSymbols.VisibilityModifierSymbol;
import org.java2uml.java2umlapi.visitors.Visitor;

import static org.java2uml.java2umlapi.util.umlSymbols.Separator.DOTTED;
import static org.java2uml.java2umlapi.util.umlSymbols.TypeDeclarationSymbol.getTypeDeclarationSymbol;
import static org.java2uml.java2umlapi.visitors.umlExtractor.UMLExtractorUtilMethods.*;

/**
 * <p>
 * UMLExtractor is visitor class which extracts plant uml code from ParsedComponents.
 * </p>
 *
 * @author kawaiifox
 */
public class UMLExtractor implements Visitor<String> {
    /**
     * Visits passed component and performs some operation on it.
     *
     * @param sourceComponent sourceComponent representing project.
     * @return Object of type R
     */
    @Override
    public String visit(SourceComponent sourceComponent) {
        return StartEnd.START.toString()
                + "\n" + generateUMLClasses(sourceComponent.getChildren(),
                sourceComponent.getExternalComponents(),
                this)
                + "\n" + generateUMLTypeRelations(sourceComponent.getAllRelations(), this)
                + "\n" + StartEnd.END;
    }

    /**
     * Visits passed component and performs some operation on it.
     *
     * @param parsedClassOrInterfaceComponent parsedClassOrInterfaceComponent representing ClassOrInterface.
     * @return Object of type R
     */
    @Override
    public String visit(ParsedClassOrInterfaceComponent parsedClassOrInterfaceComponent) {
        var resolvedDeclaration = getResolvedDeclaration(parsedClassOrInterfaceComponent);
        var children = parsedClassOrInterfaceComponent.getChildren();

        return getTypeDeclarationSymbol(resolvedDeclaration.asType()) + " {\n"
                + generateUMLFieldDeclarations(children, this)
                + generateUMLConstructorSignatures(children, this)
                + generateUMLMethodSignatures(children, this)
                + "}";
    }



    /**
     * Visits passed component and performs some operation on it.
     *
     * @param parsedEnumComponent parsedEnumComponent representing Enum.
     * @return Object of type R
     */
    @Override
    public String visit(ParsedEnumComponent parsedEnumComponent) {
        var children = parsedEnumComponent.getChildren();

        return "enum " + parsedEnumComponent.getName() + " {\n"
                + DOTTED + "Enum Constants" + DOTTED + "\n"
                + generateUMLEnumConstantDecl(children, this)
                + DOTTED + "Fields" + DOTTED + "\n"
                + generateUMLFieldDeclarations(children, this)
                + DOTTED + "Methods" + DOTTED + "\n"
                + generateUMLConstructorSignatures(children, this)
                + generateUMLMethodSignatures(children, this)
                + "}";
    }

    /**
     * Visits passed component and performs some operation on it.
     *
     * @param parsedMethodComponent parsedMethodComponent representing Method.
     * @return Object of type R
     */
    @Override
    public String visit(ParsedMethodComponent parsedMethodComponent) {
        var resolvedDeclaration = parsedMethodComponent.getResolvedMethodDeclaration();

        return VisibilityModifierSymbol.of(resolvedDeclaration.accessSpecifier().asString()) + " "
                + UMLModifier.METHOD + " "
                + (resolvedDeclaration.isStatic() ? UMLModifier.STATIC + " " : "")
                + resolvedDeclaration.getSignature() + ": " + parsedMethodComponent.getReturnTypeName();
    }

    /**
     * Visits passed component and performs some operation on it.
     *
     * @param parsedFieldComponent parsedFieldComponent representing Field.
     * @return Object of type R
     */
    @Override
    public String visit(ParsedFieldComponent parsedFieldComponent) {
        var resolvedDeclaration = parsedFieldComponent.getResolvedFieldDeclaration();

        return VisibilityModifierSymbol.of(resolvedDeclaration.accessSpecifier().asString()) + " " +
                getClassOfField(resolvedDeclaration) + " "
                + (resolvedDeclaration.isStatic() ? UMLModifier.STATIC : "")
                + " " + resolvedDeclaration.getName();
    }


    /**
     * Visits passed component and performs some operation on it.
     *
     * @param parsedConstructorComponent parsedConstructorComponent representing Constructor.
     * @return Object of type R
     */
    @Override
    public String visit(ParsedConstructorComponent parsedConstructorComponent) {
        var resolvedDeclaration = parsedConstructorComponent
                .getResolvedConstructorDeclaration();

        return VisibilityModifierSymbol.of(resolvedDeclaration.accessSpecifier().asString()) + " "
                + UMLModifier.METHOD + " [Constructor] " + resolvedDeclaration.getSignature();
    }

    /**
     * Visits passed component and performs some operation on it.
     *
     * @param parsedExternalComponent parsedExternalComponent representing External Class or interface or enum.
     * @return Object of type R
     */
    @Override
    public String visit(ParsedExternalComponent parsedExternalComponent) {
        return getTypeDeclarationSymbol(parsedExternalComponent.getResolvedTypeDeclaration()) + " {\n}";
    }

    /**
     * Visits passed component and performs some operation on it.
     *
     * @param parsedEnumConstantComponent parsedEnumConstantComponent representing Enum constant.
     * @return Object of type R
     */
    @Override
    public String visit(ParsedEnumConstantComponent parsedEnumConstantComponent) {
        return parsedEnumConstantComponent.getResolvedEnumConstantDeclaration().getName();
    }

    /**
     * Visits passed TypeRelation and performs some operation on it.
     *
     * @param typeRelation typeRelation representing relation b/w two classes.
     */
    @Override
    public String visit(TypeRelation typeRelation) {
        return typeRelation.getFrom().getName() + " " +
                typeRelation.getRelationsType() + " " + typeRelation.getTo().getName();
    }
}
