package org.java2uml.java2umlapi.lightWeight;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>
 * A lightweight data class containing all necessary information for a particular class or interface in source files.
 * </p>
 *
 * @author kawaiifox
 */

@Entity
public class ClassOrInterface implements LightWeight {
    @Id
    @GeneratedValue
    private Long id;

    @Column(columnDefinition = "varchar(500)")
    private String name;
    private boolean isClass;
    private boolean isExternal;

    @JsonIgnore
    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Constructor> constructors;

    @JsonIgnore
    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Method> methods;

    @JsonIgnore
    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Field> fields;

    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TypeParam> typeParameters;

    @JsonIgnore
    @OneToOne(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Body body;
    private Long sourceId;

    protected ClassOrInterface() {
        constructors = new ArrayList<>();
        methods = new ArrayList<>();
        fields = new ArrayList<>();
        typeParameters = new ArrayList<>();
    }

    public ClassOrInterface(String name, boolean isClass, boolean isExternal, Body body) {
        this.name = name;
        this.isClass = isClass;
        this.isExternal = isExternal;
        this.typeParameters = new ArrayList<>();
        this.constructors = new ArrayList<>();
        this.methods = new ArrayList<>();
        this.fields = new ArrayList<>();
        this.body = body;
    }

    public ClassOrInterface(String name, boolean isClass, boolean isExternal,
                            List<Constructor> constructors, List<Method> methods,
                            List<Field> fields, List<TypeParam> typeParameters,
                            Body body) {
        this.name = name;
        this.isClass = isClass;
        this.isExternal = isExternal;
        this.constructors = constructors;
        this.methods = methods;
        this.fields = fields;
        this.typeParameters = typeParameters;
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public List<Constructor> getConstructors() {
        return constructors;
    }

    public List<Method> getMethods() {
        return methods;
    }

    public List<Field> getFields() {
        return fields;
    }

    public List<TypeParam> getTypeParameters() {
        return typeParameters;
    }

    public Long getId() {
        return id;
    }

    public Body getBody() {
        return body;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public boolean isExternal() {
        return isExternal;
    }

    public boolean isGeneric() {
        return !typeParameters.isEmpty();
    }

    public boolean isClass() {
        return isClass;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setClass(boolean isClass) {
        this.isClass = isClass;
    }

    public void setConstructors(List<Constructor> constructors) {
        this.constructors = constructors;
    }

    public void setMethods(List<Method> methods) {
        this.methods = methods;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public void setTypeParameters(List<TypeParam> typeParameters) {
        this.typeParameters = typeParameters;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public void setExternal(boolean external) {
        isExternal = external;
    }

    @Override
    public Optional<ClassOrInterface> asClassOrInterface() {
        return Optional.of(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClassOrInterface)) return false;
        ClassOrInterface that = (ClassOrInterface) o;
        return isClass() == that.isClass() && getName().equals(that.getName())
                && Objects.equals(getConstructors(),
                that.getConstructors()) && Objects.equals(getMethods(),
                that.getMethods()) && Objects.equals(getFields(),
                that.getFields()) && getTypeParameters().equals(that.getTypeParameters()) && Objects.equals(getBody(),
                that.getBody()) && Objects.equals(getSourceId(), that.getSourceId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), isClass(), getConstructors(), getMethods(),
                getFields(), getTypeParameters(), getBody(), getSourceId());
    }
}
