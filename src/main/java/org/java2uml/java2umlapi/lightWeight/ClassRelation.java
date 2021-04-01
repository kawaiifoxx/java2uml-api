package org.java2uml.java2umlapi.lightWeight;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClassRelation {
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private LightWeight from;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private LightWeight to;

    private RelationsSymbol relationsSymbol;

    @Id
    @GeneratedValue
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Source parent;

    public ClassRelation(LightWeight from, LightWeight to, RelationsSymbol relationsSymbol, Source parent) {
        this.from = from;
        this.to = to;
        this.relationsSymbol = relationsSymbol;
        this.parent = parent;
    }

    protected ClassRelation() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public LightWeight getFrom() {
        return from;
    }

    public void setFrom(LightWeight from) {
        this.from = from;
    }

    public LightWeight getTo() {
        return to;
    }

    public void setTo(LightWeight to) {
        this.to = to;
    }

    public RelationsSymbol getRelationsSymbol() {
        return relationsSymbol;
    }

    public void setRelationsSymbol(RelationsSymbol relationsSymbol) {
        this.relationsSymbol = relationsSymbol;
    }

    public Source getParent() {
        return parent;
    }

    public void setParent(Source parent) {
        this.parent = parent;
    }
}
