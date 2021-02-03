package org.java2uml.java2umlapi.umlComponenets;

import com.github.javaparser.resolution.declarations.ResolvedDeclaration;
import org.java2uml.java2umlapi.util.umlSymbols.UMLModifier;

import java.util.Optional;

import static org.java2uml.java2umlapi.util.StaticParsedComponentsUtil.getVisibilityModifierSymbol;

/**
 * <p>
 * Leaf Component, representing method declaration, in a parsed java src code.
 * </p>
 *
 * @author kawaiifox
 */
public class ParsedMethodComponent implements ParsedComponent {

    private final ParsedComponent parent;
    private final ResolvedDeclaration resolvedDeclaration;
    private final String printableName;
    private final String name;

    public ParsedMethodComponent(ParsedComponent parent, ResolvedDeclaration resolvedDeclaration) {
        this.parent = parent;
        this.resolvedDeclaration = resolvedDeclaration;
        this.printableName = resolvedDeclaration.asMethod().getSignature();
        this.name = resolvedDeclaration.asMethod().getQualifiedSignature();
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

    @Override
    public String toString() {
        return getVisibilityModifierSymbol(resolvedDeclaration) + " "
                + UMLModifier.METHOD + " "
                + (resolvedDeclaration.asMethod().isStatic() ? UMLModifier.STATIC : "")
                + " " + printableName;
    }

}
