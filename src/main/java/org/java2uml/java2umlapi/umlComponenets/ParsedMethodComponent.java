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
    private ResolvedMethodDeclaration resolvedDeclaration;
    private final String printableName;
    private final String name;

    /**
     * Initializes ParsedMethodComponent.
     * @param parent Parent of this component.
     * @param resolvedDeclaration resolvedMethodDeclaration is type solved method declaration
     *                            retrieved from resolvedReferenceTypeDeclaration.
     */
    public ParsedMethodComponent(ParsedComponent parent, ResolvedMethodDeclaration resolvedDeclaration) {
        this.parent = parent;
        this.resolvedDeclaration = resolvedDeclaration;
        this.printableName = resolvedDeclaration.getSignature();
        this.name = resolvedDeclaration.getQualifiedSignature();
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
        return name;
    }

    /**
     * @return returns the uml form of this component.
     */
    @Override
    public String toUML() {
        return VisibilityModifierSymbol.of(resolvedDeclaration.accessSpecifier().asString()) + " "
                + UMLModifier.METHOD + " "
                + (resolvedDeclaration.isStatic() ? UMLModifier.STATIC : "")
                + " " + printableName;
    }

    @Override
    public String toString() {
        return "ParsedMethodComponent{" +
                ", printableName='" + printableName + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
