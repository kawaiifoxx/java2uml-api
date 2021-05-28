package org.java2uml.java2umlapi.lightWeight;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
/**
 * <p>
 * An Entity Class representing an enum in source files.
 * </p>
 *
 * @author kawaiifox
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnumLW extends LightWeight {
    @Column(columnDefinition = "varchar(1000)")
    private String name;

    @Column(columnDefinition = "varchar(500)")
    private String packageName;

    @JsonIgnore
    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<EnumConstant> enumConstants;

    @JsonIgnore
    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Constructor> enumConstructors;

    @JsonIgnore
    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Method> enumMethods;

    @JsonIgnore
    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Field> enumFields;

    @JsonIgnore
    @OneToOne(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Body body;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    private LightWeight parent;

    public EnumLW(String name) {
        this.name = name;
    }

    public EnumLW(String name, List<EnumConstant> enumConstants,
                  List<Constructor> constructors, List<Method> methods,
                  List<Field> fields, Body body) {
        this.name = name;
        this.enumConstants = enumConstants;
        this.enumConstructors = constructors;
        this.enumMethods = methods;
        this.enumFields = fields;
        this.body = body;
    }

    protected EnumLW() {
        this.enumConstants = new ArrayList<>();
        this.enumConstructors = new ArrayList<>();
        this.enumMethods = new ArrayList<>();
        this.enumFields = new ArrayList<>();
    }

    public void addEnumConstant(EnumConstant enumConstant) {
        enumConstants.add(enumConstant);
    }

    public void addConstructor(Constructor constructor) {
        enumConstructors.add(constructor);
    }

    public void addMethod(Method method) {
        enumMethods.add(method);
    }

    @Override
    public String getName() {
        return name;
    }

    public List<EnumConstant> getEnumConstants() {
        return enumConstants;
    }

    public List<Constructor> getEnumConstructors() {
        return enumConstructors;
    }

    public List<Method> getEnumMethods() {
        return enumMethods;
    }

    public Body getBody() {
        return body;
    }

    public List<Field> getEnumFields() {
        return enumFields;
    }

    @Override
    public LightWeight getParent() {
        return parent;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEnumConstants(List<EnumConstant> enumConstants) {
        this.enumConstants = enumConstants;
    }

    public void setEnumConstructors(List<Constructor> constructors) {
        this.enumConstructors = constructors;
    }

    public void setEnumMethods(List<Method> methods) {
        this.enumMethods = methods;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public void setEnumFields(List<Field> fields) {
        this.enumFields = fields;
    }

    public void setParent(LightWeight parent) {
        this.parent = parent;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public Optional<EnumLW> asEnumLW() {
        return Optional.of(this);
    }
}
