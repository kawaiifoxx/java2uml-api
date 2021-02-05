package org.java2uml.java2umlapi.umlComponenets;


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
    private final String relationsType;

    public TypeRelation(ParsedComponent from, ParsedComponent to, String relationsType) {

        var isFromParsedClassOrInterfaceOrExtAncestor = from.isParsedClassOrInterfaceComponent() || from.isParsedExternalAncestor();
        var isToParsedClassOrInterfaceOrExtAncestor = to.isParsedClassOrInterfaceComponent() || to.isParsedExternalAncestor();

        if (!isFromParsedClassOrInterfaceOrExtAncestor || !isToParsedClassOrInterfaceOrExtAncestor) {
            throw new RuntimeException("[TypeRelation] Passed ParsedComponent should be a ParsedClassOrInterfaceComponent or ParsedExternalAncestor.");
        }

        this.from = from;
        this.to = to;
        this.relationsType = relationsType;
    }

    public java.lang.String toUML() {
        if (from.getResolvedDeclaration().isEmpty() || to.getResolvedDeclaration().isEmpty()) {
            throw new RuntimeException("Unable to get ResolvedDeclaration, because from or to returned empty Optional.");
        }

        java.lang.String fromClassDecl = from
                .getResolvedDeclaration()
                .get()
                .asType()
                .getQualifiedName();
        java.lang.String toClassDecl = to
                .getResolvedDeclaration()
                .get()
                .asType()
                .getQualifiedName();

        return fromClassDecl + " " + relationsType + " " + toClassDecl;
    }

    @Override
    public java.lang.String toString() {
        return "TypeRelation{" +
                "from=" + from +
                ", to=" + to +
                ", relationsType=" + relationsType +
                '}';
    }
}
