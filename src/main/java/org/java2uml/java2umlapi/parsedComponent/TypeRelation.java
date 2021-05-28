package org.java2uml.java2umlapi.parsedComponent;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.java2uml.java2umlapi.util.umlSymbols.RelationsSymbol;
import org.java2uml.java2umlapi.visitors.Visitor;

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
    private final RelationsSymbol relationsSymbol;

    /**
     * Initializes TypeRelation with ParsedComponent from, to and relationsType.
     * @param from ParsedCompositeComponent from which relation is defined.
     * @param to ParsedCompositeComponent to which relation is defined.
     * @param relationsType type of relation, for e.g. ASSOCIATION, AGGREGATION, DEPENDENCY, EXTENSION.
     */
    public TypeRelation(ParsedCompositeComponent from, ParsedCompositeComponent to, String relationsType, RelationsSymbol relationsSymbol) {
        this.from = from;
        this.to = to;
        this.relationsType = relationsType;
        this.relationsSymbol = relationsSymbol;
    }

    /**
     * @return From ParsedCompositeComponent.
     */
    public ParsedCompositeComponent getFrom() {
        return from;
    }

    /**
     * @return To ParsedCompositeComponent.
     */
    public ParsedCompositeComponent getTo() {
        return to;
    }

    /**
     * @return Type of relation.
     */
    public String getRelationsType() {
        return relationsType;
    }

    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof TypeRelation)) return false;

        TypeRelation that = (TypeRelation) o;

        return new EqualsBuilder()
                .append(getFrom(), that.getFrom()).append(getTo(), that.getTo())
                .append(getRelationsType(), that.getRelationsType())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getFrom())
                .append(getTo())
                .append(getRelationsType())
                .toHashCode();
    }

    @Override
    public String toString() {
        return "TypeRelation{" +
                "from=" + from.getName() +
                ", to=" + to.getName() +
                ", relationsType=" + relationsType +
                '}';
    }

    public RelationsSymbol getRelationsSymbol() {
        return relationsSymbol;
    }
}
