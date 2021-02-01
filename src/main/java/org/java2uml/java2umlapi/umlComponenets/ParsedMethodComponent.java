package org.java2uml.java2umlapi.umlComponenets;

import com.github.javaparser.resolution.declarations.ResolvedDeclaration;

import java.util.List;
import java.util.Optional;

public class ParsedMethodComponent  implements ParsedComponent{

    private final ParsedComponent parent;
    private final ResolvedDeclaration resolvedDeclaration;

    public ParsedMethodComponent(ParsedComponent parent, ResolvedDeclaration resolvedDeclaration) {
        this.parent = parent;
        this.resolvedDeclaration = resolvedDeclaration;
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


}
