package org.java2uml.java2umlapi.umlComponenets;

import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;

import java.util.List;
import java.util.Optional;

public class SourceComponent implements ParsedComponent {

    private List<ParsedComponent> children;
    private final List<ResolvedReferenceTypeDeclaration> allParsedTypes;

    public SourceComponent(List<ResolvedReferenceTypeDeclaration> allParsedTypes) {
        this.allParsedTypes = allParsedTypes;
    }

    @Override
    public boolean isSourceComponent() {
        return true;
    }

    @Override
    public Optional<SourceComponent> asSourceComponent() {
        return Optional.of(this);
    }

    @Override
    public Optional<ParsedComponent> getParentComponent() {
        return Optional.empty();
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
