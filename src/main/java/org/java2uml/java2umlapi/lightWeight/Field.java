package org.java2uml.java2umlapi.lightWeight;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.util.Optional;

/**
 * <p>
 * An Entity Class representing a field in source files.
 * </p>
 *
 * @author kawaiifox
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Field extends LightWeight {
    @Column(columnDefinition = "varchar(500)")
    private String typeName;
    @Column(columnDefinition = "varchar(1000)")
    private String name;
    @Column(columnDefinition = "varchar(10)")
    private String visibility;
    private boolean isStatic;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private LightWeight parent;

    protected Field() {
    }

    public Field(String typeName, String name, String visibility, boolean isStatic) {
        this.typeName = typeName;
        this.name = name;
        this.visibility = visibility;
        this.isStatic = isStatic;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    /**
     * if this light weight is Field then a Field is returned.
     *
     * @return Field
     */
    @Override
    public Optional<Field> asField() {
        return Optional.of(this);
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }

    @Override
    public LightWeight getParent() {
        return parent;
    }

    public void setParent(LightWeight parent) {
        this.parent = parent;
    }
}
