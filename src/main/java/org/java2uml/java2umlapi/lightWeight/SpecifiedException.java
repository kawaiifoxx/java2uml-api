package org.java2uml.java2umlapi.lightWeight;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

/**
 * <p>
 * An Entity Class representing specified exceptions on a method or a constructor.
 * </p>
 *
 * @author kawaiifox
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpecifiedException extends LightWeight {
    @Column(columnDefinition = "varchar(1000)")
    private String name;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private LightWeight parent;

    protected SpecifiedException() {
    }

    public SpecifiedException(String name, LightWeight parent) {
        this.name = name;
        this.parent = parent;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParent(LightWeight parent) {
        this.parent = parent;
    }

    @Override
    public LightWeight getParent() {
        return parent;
    }
}
