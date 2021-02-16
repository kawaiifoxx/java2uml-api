package org.java2uml.java2umlapi.umlComponenets;


/**
 * <p>
 * This class defines relation between, two types. It is mainly used for uml code generation.
 * </p>
 *
 * @author kawaiifox
 */
public class TypeRelation {
    private final ParsedCompositeComponent from;
    private final ParsedCompositeComponent to;
    private final String relationsType;

    /**
     * Initializes TypeRelation with ParsedComponent from, to and relationsType.
     * @param from ParsedCompositeComponent from which relation is defined.
     * @param to ParsedCompositeComponent to which relation is defined.
     * @param relationsType type of relation, for e.g. ASSOCIATION, AGGREGATION, DEPENDENCY, EXTENSION.
     */
    public TypeRelation(ParsedCompositeComponent from, ParsedCompositeComponent to, String relationsType) {
        this.from = from;
        this.to = to;
        this.relationsType = relationsType;
    }

    /**
     * @return Returns generated UML syntax.
     */
    public String toUML() {
        return from.getName() + " " + relationsType + " " + to.getName();
    }

    @Override
    public String toString() {
        return "TypeRelation{" +
                "from=" + from.getName() +
                ", to=" + to.getName() +
                ", relationsType=" + relationsType +
                '}';
    }
}
