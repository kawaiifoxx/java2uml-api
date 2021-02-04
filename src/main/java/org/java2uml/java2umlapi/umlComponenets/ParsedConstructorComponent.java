package org.java2uml.java2umlapi.umlComponenets;

import com.github.javaparser.resolution.declarations.ResolvedConstructorDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedDeclaration;
import org.java2uml.java2umlapi.util.umlSymbols.UMLModifier;
import org.java2uml.java2umlapi.util.umlSymbols.VisibilityModifierSymbol;

import java.util.Optional;

public class ParsedConstructorComponent implements ParsedComponent {
    private final ResolvedConstructorDeclaration resolvedDeclaration;
    private final ParsedComponent parent;
    private final String printableName;

    public ParsedConstructorComponent(ParsedComponent parent, ResolvedConstructorDeclaration resolvedDeclaration) {
        this.resolvedDeclaration = resolvedDeclaration;
        this.parent = parent;
        this.printableName = resolvedDeclaration.getSignature();
    }

    @Override
    public Optional<ResolvedDeclaration> getResolvedDeclaration() {
        return Optional.of(resolvedDeclaration);
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public boolean isParsedConstructorComponent() {
        return true;
    }

    @Override
    public Optional<ParsedConstructorComponent> asParsedConstructorComponent() {
        return Optional.of(this);
    }

    @Override
    public Optional<ParsedComponent> getParent() {
        return Optional.of(parent);
    }

    @Override
    public String getName() {
        return printableName;
    }

    @Override
    public String toUML() {
        return VisibilityModifierSymbol.of(resolvedDeclaration.accessSpecifier().asString()) + " "
                + UMLModifier.METHOD + " [Constructor] " + printableName;
    }

    @Override
    public String toString() {
        return "ParsedConstructorComponent{" +
                "resolvedDeclaration=" + resolvedDeclaration +
                ", printableName='" + printableName + '\'' +
                '}';
    }
}
