package org.java2uml.java2umlapi.umlComponenets;

import com.github.javaparser.resolution.declarations.ResolvedDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;

import java.util.List;
import java.util.Optional;

/**
 *
 */
public class ParsedClassComponent implements ParsedComponent {

    private final ResolvedDeclaration resolvedReferenceTypeDeclaration;

    private  final ParsedComponent parent;

    private List<ParsedComponent> children;


    public ParsedClassComponent(ResolvedDeclaration resolvedReferenceTypeDeclaration, ParsedComponent parent) {
        this.resolvedReferenceTypeDeclaration = resolvedReferenceTypeDeclaration;
        this.parent = parent;
    }

    @Override
    public Optional<ResolvedDeclaration> getResolvedDeclaration() {
        return Optional.of(resolvedReferenceTypeDeclaration);
    }

    @Override
    public Optional<ParsedComponent> getParent() {
        return Optional.of(parent);
    }

    @Override
    public Optional<List<ParsedComponent>> getChildren() {
        if (children == null)
            return Optional.empty();
        return Optional.of(children);
    }

    @Override
    public String toString() {
        return "to be implemented";
    }
}
