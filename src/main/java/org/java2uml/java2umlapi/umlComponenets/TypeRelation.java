package org.java2uml.java2umlapi.umlComponenets;

import org.java2uml.java2umlapi.util.umlSymbols.RelationsSymbol;

/**
 * <p>
 * This class defines relation between, two types. It is mainly used for uml code generation.
 * </p>
 *
 * @author kawaiifox
 */
public class TypeRelation {
    private final ParsedComponent from;
    private final ParsedComponent to;
    private final RelationsSymbol relationsType;

    public TypeRelation(ParsedComponent from, ParsedComponent to, RelationsSymbol relationsType) {
        this.from = from;
        this.to = to;
        this.relationsType = relationsType;
    }

    @Override
    public String toString() {
        if (from.getResolvedDeclaration().isEmpty() && to.getResolvedDeclaration().isEmpty()) {
            throw new RuntimeException("Unable to get ResolvedDeclaration, because from or to returned empty Optional.");
        }

        String fromClassDecl = from
                .getResolvedDeclaration()
                .get()
                .asType()
                .getQualifiedName();
        String toClassDecl = to
                .getResolvedDeclaration()
                .get()
                .asType()
                .getQualifiedName();

        return fromClassDecl + " " + relationsType + " " + toClassDecl;
    }
}
