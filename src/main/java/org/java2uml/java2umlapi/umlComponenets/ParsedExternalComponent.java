package org.java2uml.java2umlapi.umlComponenets;

import com.github.javaparser.resolution.declarations.ResolvedDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedTypeDeclaration;

import java.util.Optional;

import static org.java2uml.java2umlapi.util.umlSymbols.TypeDeclarationSymbol.getTypeDeclarationSymbol;

public class ParsedExternalComponent implements ParsedComponent {

    private final ResolvedTypeDeclaration resolvedTypeDeclaration;

    private String typeDeclaration;
    private final String name;

    public ParsedExternalComponent(ResolvedTypeDeclaration resolvedTypeDeclaration) {
        this.resolvedTypeDeclaration = resolvedTypeDeclaration;
        this.name = resolvedTypeDeclaration.getQualifiedName();
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public boolean isParsedExternalAncestor() {
        return true;
    }

    @Override
    public Optional<ParsedComponent> getParent() {
        return Optional.empty();
    }

    @Override
    public Optional<ResolvedDeclaration> getResolvedDeclaration() {
        return Optional.of(resolvedTypeDeclaration);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toUML() {
        if (typeDeclaration == null) {
            typeDeclaration = getTypeDeclarationSymbol(resolvedTypeDeclaration);
        }

        return typeDeclaration + " {\n}" ;
    }
}
