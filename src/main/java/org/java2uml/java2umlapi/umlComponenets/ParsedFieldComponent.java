package org.java2uml.java2umlapi.umlComponenets;

import com.github.javaparser.resolution.declarations.ResolvedDeclaration;
import org.java2uml.java2umlapi.util.umlSymbols.UMLModifier;

import java.util.List;
import java.util.Optional;

import static org.java2uml.java2umlapi.util.StaticParsedComponentsUtil.getClassOfField;

public class ParsedFieldComponent implements ParsedComponent {
    private final ParsedComponent parent;
    private final ResolvedDeclaration resolvedDeclaration;
    private final String printableName;

    public ParsedFieldComponent(ParsedComponent parent, ResolvedDeclaration resolvedDeclaration) {
        this.parent = parent;
        this.resolvedDeclaration = resolvedDeclaration;
        this.printableName = resolvedDeclaration.asField().getName();
    }

    @Override
    public boolean isLeaf() {
        return true;
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
    public Optional<List<ParsedComponent>> getChildren() {
        return Optional.empty();
    }

    @Override
    public String toString() {

        return getClassOfField(resolvedDeclaration) + " "
                + (resolvedDeclaration.asField().isStatic() ? UMLModifier.STATIC : "")
                + " " + printableName;
    }
}
