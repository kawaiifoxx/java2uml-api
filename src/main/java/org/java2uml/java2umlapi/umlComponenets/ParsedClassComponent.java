package org.java2uml.java2umlapi.umlComponenets;

import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;

import java.util.List;
import java.util.Optional;

/**
 *
 */
public class ParsedClassComponent implements ParsedComponent {

    private final ResolvedReferenceTypeDeclaration resolvedReferenceTypeDeclaration;

    private  final ParsedComponent parent;

    private List<ParsedComponent> children;


    public ParsedClassComponent(ResolvedReferenceTypeDeclaration resolvedReferenceTypeDeclaration, ParsedComponent parent) {
        this.resolvedReferenceTypeDeclaration = resolvedReferenceTypeDeclaration;
        this.parent = parent;
    }

    @Override
    public Optional<ResolvedReferenceTypeDeclaration> getResolvedReferenceTypeDeclaration() {
        return Optional.of(resolvedReferenceTypeDeclaration);
    }

    @Override
    public Optional<ParsedComponent> getParentComponent() {
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
