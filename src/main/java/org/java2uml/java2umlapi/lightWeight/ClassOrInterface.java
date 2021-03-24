package org.java2uml.java2umlapi.lightWeight;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClassOrInterface extends LightWeight {
    @Column(columnDefinition = "varchar(500)")
    private String name;
    @Column(columnDefinition = "varchar(500)")
    private String packageName;
    private boolean isClass;
    private boolean isExternal;

    @JsonIgnore
    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Constructor> classConstructors;

    @JsonIgnore
    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Method> classOrInterfaceMethods;

    @JsonIgnore
    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Field> classFields;

    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TypeParam> classOrInterfaceTypeParameters;

    @JsonIgnore
    @OneToOne(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Body body;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    private LightWeight parent;

    protected ClassOrInterface() {
    }

    public ClassOrInterface(String name,boolean isClass, boolean isExternal, Body body) {
        this.name = name;
        this.isClass = isClass;
        this.isExternal = isExternal;
        this.classOrInterfaceTypeParameters = new ArrayList<>();
        this.classConstructors = new ArrayList<>();
        this.classOrInterfaceMethods = new ArrayList<>();
        this.classFields = new ArrayList<>();
        this.body = body;
    }

    public ClassOrInterface(String name, boolean isClass, boolean isExternal) {
        this.name = name;
        this.isClass = isClass;
        this.isExternal = isExternal;
    }

    public ClassOrInterface(String name, String packageName, boolean isClass, boolean isExternal) {
        this.name = name;
        this.isClass = isClass;
        this.packageName = packageName;
        this.isExternal = isExternal;
    }

    public ClassOrInterface(String name, boolean isClass, boolean isExternal,
                            List<Constructor> classConstructors, List<Method> methods,
                            List<Field> fields, List<TypeParam> typeParameters,
                            Body body) {
        this.name = name;
        this.isClass = isClass;
        this.isExternal = isExternal;
        this.classConstructors = classConstructors;
        this.classOrInterfaceMethods = methods;
        this.classFields = fields;
        this.classOrInterfaceTypeParameters = typeParameters;
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public List<Constructor> getClassConstructors() {
        return classConstructors;
    }

    public List<Method> getClassOrInterfaceMethods() {
        return classOrInterfaceMethods;
    }

    public List<Field> getClassFields() {
        return classFields;
    }

    public List<TypeParam> getClassOrInterfaceTypeParameters() {
        return classOrInterfaceTypeParameters;
    }

    public Body getBody() {
        return body;
    }

    public boolean isExternal() {
        return isExternal;
    }

    public boolean isGeneric() {
        return !classOrInterfaceTypeParameters.isEmpty();
    }

    public boolean isClass() {
        return isClass;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setClass(boolean isClass) {
        this.isClass = isClass;
    }

    public void setClassConstructors(List<Constructor> constructors) {
        this.classConstructors = constructors;
    }

    public void setClassOrInterfaceMethods(List<Method> methods) {
        this.classOrInterfaceMethods = methods;
    }

    public void setClassFields(List<Field> fields) {
        this.classFields = fields;
    }

    public void setClassOrInterfaceTypeParameters(List<TypeParam> typeParameters) {
        this.classOrInterfaceTypeParameters = typeParameters;
    }

    public void setBody(Body body) {
        this.body = body;
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
        return isClass() == that.isClass() &&
                isExternal() == that.isExternal() &&
                Objects.equals(getId(), that.getId()) &&
                getName().equals(that.getName()) &&
                Objects.equals(getClassConstructors(), that.getClassConstructors()) &&
                Objects.equals(getClassOrInterfaceMethods(), that.getClassOrInterfaceMethods()) &&
                Objects.equals(getClassFields(), that.getClassFields()) &&
                Objects.equals(getClassOrInterfaceTypeParameters(), that.getClassOrInterfaceTypeParameters()) &&
                Objects.equals(getBody(), that.getBody()) &&
                Objects.equals(getParent(), that.getParent());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), isClass(), isExternal());
    }

    @Override
    public LightWeight getParent() {
        return parent;
    }

    public void setParent(LightWeight parent) {
        this.parent = parent;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
