package org.java2uml.java2umlapi.lightWeight;

import org.java2uml.java2umlapi.util.umlSymbols.RelationsSymbol;

import javax.persistence.*;
/**
 * <p>
 * An Entity Class representing relationships b/w different classes.
 * </p>
 *
 * @author kawaiifox
 */
@Entity
public class ClassRelation {
    @ManyToOne(fetch = FetchType.EAGER)
    private ClassOrInterface from;

    @ManyToOne(fetch = FetchType.EAGER)
    private ClassOrInterface to;

    private RelationsSymbol relationsSymbol;

    @Id
    @GeneratedValue
    private Long id;

    public ClassRelation(ClassOrInterface from, ClassOrInterface to, RelationsSymbol relationsSymbol) {
        this.from = from;
        this.to = to;
        this.relationsSymbol = relationsSymbol;
    }

    protected ClassRelation() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public ClassOrInterface getFrom() {
        return from;
    }

    public void setFrom(ClassOrInterface from) {
        this.from = from;
    }

    public ClassOrInterface getTo() {
        return to;
    }

    public void setTo(ClassOrInterface to) {
        this.to = to;
    }

    public RelationsSymbol getRelationsSymbol() {
        return relationsSymbol;
    }

    public void setRelationsSymbol(RelationsSymbol relationsSymbol) {
        this.relationsSymbol = relationsSymbol;
    }
}
