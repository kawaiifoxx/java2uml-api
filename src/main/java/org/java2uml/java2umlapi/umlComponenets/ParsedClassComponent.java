package org.java2uml.java2umlapi.umlComponenets;

import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;

public class ParsedClassComponent implements ParsedComponent {

    private ResolvedReferenceTypeDeclaration resolvedReferenceTypeDeclaration;

    public ParsedClassComponent() {
    }

    public ParsedClassComponent(ResolvedReferenceTypeDeclaration resolvedReferenceTypeDeclaration) {
        this.resolvedReferenceTypeDeclaration = resolvedReferenceTypeDeclaration;
    }

    @Override
    public ResolvedReferenceTypeDeclaration getResolvedReferenceTypeDeclaration() {
        return resolvedReferenceTypeDeclaration;
    }
}
