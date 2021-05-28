package org.java2uml.java2umlapi.lightWeight;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

/**
 * <p>
 * A lightweight data class containing all necessary information for a particular parameter in source files.
 * </p>
 *
 * @author kawaiifox
 */

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Param extends LightWeight {
    @Column(columnDefinition = "varchar(500)")
    private String typeName;
    @Column(columnDefinition = "varchar(1000)")
    private String name;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private LightWeight parent;

    protected Param() {
    }

    public Param(String typeName, String name) {
        this.typeName = typeName;
        this.name = name;
    }

    public Param(String typeName, String name, LightWeight parent) {
        this.typeName = typeName;
        this.name = name;
        this.parent = parent;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public LightWeight getParent() {
        return parent;
    }

    public void setParent(LightWeight parent) {
        this.parent = parent;
    }
}
