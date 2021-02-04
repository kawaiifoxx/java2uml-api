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

        var isFromParsedClassOrInterfaceOrExtAncestor = from.isParsedClassOrInterfaceComponent() || from.isParsedExternalAncestor();
        var isToParsedClassOrInterfaceOrExtAncestor = to.isParsedClassOrInterfaceComponent() || to.isParsedExternalAncestor();

        if (!isFromParsedClassOrInterfaceOrExtAncestor || !isToParsedClassOrInterfaceOrExtAncestor) {
            throw new RuntimeException("[TypeRelation] Passed ParsedComponent should be a ParsedClassOrInterfaceComponent or ParsedExternalAncestor.");
        }

        this.from = from;
        this.to = to;
        this.relationsType = relationsType;
    }

    public String toUML() {
        if (from.getResolvedDeclaration().isEmpty() || to.getResolvedDeclaration().isEmpty()) {
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

    @Override
    public String toString() {
        return "TypeRelation{" +
                "from=" + from +
                ", to=" + to +
                ", relationsType=" + relationsType +
                '}';
    }
}
