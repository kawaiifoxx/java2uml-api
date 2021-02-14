package org.java2uml.java2umlapi.umlComponenets;

import com.github.javaparser.resolution.declarations.ResolvedDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import org.java2uml.java2umlapi.util.umlSymbols.UMLModifier;
import org.java2uml.java2umlapi.util.umlSymbols.VisibilityModifierSymbol;

import java.util.Optional;

/**
 * <p>
 * Leaf Component, representing method declaration, in a parsed java src code.
 * </p>
 *
 * @author kawaiifox
 */
public class ParsedMethodComponent implements ParsedComponent {

    private final ParsedComponent parent;
    private final ResolvedMethodDeclaration resolvedDeclaration;
    private final String UMLName;
    private final String qualifiedName;
    private final String returnType;

    /**
     * Initializes ParsedMethodComponent.
     * @param parent Parent of this component.
     * @param resolvedDeclaration resolvedMethodDeclaration is type solved method declaration
     *                            retrieved from resolvedReferenceTypeDeclaration.
     */
    public ParsedMethodComponent(ParsedComponent parent, ResolvedMethodDeclaration resolvedDeclaration) {
        this.parent = parent;
        this.resolvedDeclaration = resolvedDeclaration;
        this.UMLName = resolvedDeclaration.getSignature();
        this.qualifiedName = resolvedDeclaration.getQualifiedSignature();
        this.returnType = getReturnType();
    }

    /**
     * Gets the correct return type from resolvedDeclaration and then returns it in string form.
     *
     * @return returns string of return type.
     */
    private String getReturnType() {
        var resolvedType = resolvedDeclaration.getReturnType();

        if (resolvedType.isVoid()) {
            return "void";
        }

        if (resolvedType.isReferenceType()) {
            var qualifiedReturnType = resolvedType.asReferenceType().getQualifiedName().split("\\.");
            return qualifiedReturnType[qualifiedReturnType.length - 1];
        }

        return resolvedType.asPrimitive().name().toLowerCase();
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public boolean isParsedMethodComponent() {
        return true;
    }

    @Override
    public Optional<ParsedMethodComponent> asParsedMethodComponent() {
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
    public String getName() {
        return qualifiedName;
    }

    /**
     * @return returns the return type of ParsedMethodComponent.
     */
    public String getReturnTypeName() {
        return returnType;
    }

    /**
     * @return returns the uml form of this component.
     */
    @Override
    public String toUML() {
        return VisibilityModifierSymbol.of(resolvedDeclaration.accessSpecifier().asString()) + " "
                + UMLModifier.METHOD + " "
                + (resolvedDeclaration.isStatic() ? UMLModifier.STATIC + " " : "")
                + UMLName + ": " + returnType;
    }

    @Override
    public String toString() {
        return "ParsedMethodComponent{" +
                ", printableName='" + UMLName + '\'' +
                ", name='" + qualifiedName + '\'' +
                '}';
    }
}
