package org.java2uml.java2umlapi.umlComponenets;

import com.github.javaparser.resolution.declarations.ResolvedDeclaration;

import java.util.List;
import java.util.Optional;

public class ParsedConstructorComponent implements ParsedComponent{
    private final ResolvedDeclaration resolvedDeclaration;
    private final ParsedComponent parent;

    public ParsedConstructorComponent(ResolvedDeclaration resolvedDeclaration, ParsedComponent parent) {
        this.resolvedDeclaration = resolvedDeclaration;
        this.parent = parent;
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
    public Optional<ParsedComponent> getParent() {
        return Optional.of(parent);
    }

    @Override
    public Optional<List<ParsedComponent>> getChildren() {
        return Optional.empty();
    }
}
