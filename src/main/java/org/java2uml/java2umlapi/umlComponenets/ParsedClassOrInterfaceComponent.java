package org.java2uml.java2umlapi.umlComponenets;

import com.github.javaparser.resolution.declarations.ResolvedDeclaration;
import org.java2uml.java2umlapi.util.umlSymbols.TypeDeclarationSymbol;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * <p>
 * A composite Component, containing other classes or interfaces, or methods or fields as children.
 * </p>
 *
 * @author kawaiifox
 */
public class ParsedClassOrInterfaceComponent implements ParsedComponent {

    private final ResolvedDeclaration resolvedDeclaration;

    private final ParsedComponent parent;

    private final String name;

    private String methodSignatures;

    private String constructorSignatures;

    private String fieldsDeclarations;

    private String typeDeclaration;

    private Map<String, ParsedComponent> children;


    public ParsedClassOrInterfaceComponent(ResolvedDeclaration resolvedDeclaration, ParsedComponent parent) {
        this.resolvedDeclaration = resolvedDeclaration;
        this.parent = parent;
        this.name = resolvedDeclaration.asType().asReferenceType().getQualifiedName();
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public boolean isParsedClassOrInterfaceComponent() {
        return true;
    }

    @Override
    public Optional<ParsedClassOrInterfaceComponent> asParsedClassOrInterfaceComponent() {
        return Optional.of(this);
    }

    @Override
    public Optional<ResolvedDeclaration> getResolvedDeclaration() {
        return Optional.of(resolvedDeclaration);
    }

    @Override
    public Optional<ParsedComponent> getParent() {
        return Optional.of(parent);
    }

    @Override
    public Optional<Map<String, ParsedComponent>> getChildren() {
        if (children == null)
            return Optional.empty();
        return Optional.of(children);
    }

    @Override
    public String getName() {
        return name;
    }

    public void addChild(ParsedComponent parsedComponent) {
        if (children == null) {
            children = new HashMap<>();
        }

        children.put(parsedComponent.getName(), parsedComponent);
    }

    private void generateUMLMethodSignatures() {
        StringBuilder generatedSignatures = new StringBuilder();

        children.forEach((k, v) -> {
            if (v.isParsedMethodComponent()) {
                generatedSignatures.append(v).append("\n");
            }
        });

        methodSignatures = generatedSignatures.toString();
    }

    private void generateUMLConstructorSignatures() {
        StringBuilder generatedConstructorSignatures = new StringBuilder();

        children.forEach((k, v) -> {
            if (v.isParsedConstructorComponent()) {
                generatedConstructorSignatures.append(v).append("\n");
            }
        });

        constructorSignatures = generatedConstructorSignatures.toString();
    }

    private void generateUMLFieldDeclarations() {
        StringBuilder generatedFields = new StringBuilder();

        children.forEach((k, v) -> {
            if (v.isParseFieldComponent()) {
                generatedFields.append(v).append("\n");
            }
        });

        fieldsDeclarations = generatedFields.toString();
    }

    @Override
    public String toString() {
        if (fieldsDeclarations == null) {
            generateUMLFieldDeclarations();
        }

        if (methodSignatures == null) {
            generateUMLMethodSignatures();
        }

        if (constructorSignatures == null) {
            generateUMLConstructorSignatures();
        }

        if (typeDeclaration == null) {
            getTypeDeclarationSymbol();
        }

        return typeDeclaration + " " + name + " {\n"
                + fieldsDeclarations
                + constructorSignatures
                + methodSignatures
                + "\n}";
    }

    private void getTypeDeclarationSymbol() {
        typeDeclaration = TypeDeclarationSymbol.CLASS.toString();

        if (resolvedDeclaration.asType().isClass()) {
            typeDeclaration = TypeDeclarationSymbol.CLASS.toString();
            if (resolvedDeclaration.asType().asReferenceType().isGeneric()) {
                typeDeclaration = TypeDeclarationSymbol.CLASS.parametrizeOn(getTypeParamsString().toString().trim());
            }
        } else if (resolvedDeclaration.asType().isInterface()) {
            typeDeclaration = TypeDeclarationSymbol.INTERFACE.toString();
            if (resolvedDeclaration.asType().asReferenceType().isGeneric()) {
                typeDeclaration = TypeDeclarationSymbol.INTERFACE.parametrizeOn(getTypeParamsString().toString().trim());
            }
        }
    }

    @NotNull
    private StringBuilder getTypeParamsString() {
        var typeParams = resolvedDeclaration
                .asType()
                .asReferenceType()
                .getTypeParameters();

        StringBuilder typeParamsString = new StringBuilder();

        typeParams.forEach(typeParam ->
                typeParamsString.append(typeParam.getName()).append(" "));
        return typeParamsString;
    }
}
