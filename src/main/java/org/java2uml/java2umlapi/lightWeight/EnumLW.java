package org.java2uml.java2umlapi.lightWeight;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
public class EnumLW implements LightWeight {
    @Column(columnDefinition = "varchar(500)")
    private String name;

    @JsonIgnore
    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<EnumConstant> enumConstants;

    @JsonIgnore
    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Constructor> constructors;

    @JsonIgnore
    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Method> methods;

    @JsonIgnore
    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Field> fields;

    @JsonIgnore
    @OneToOne(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Body body;

    private Long sourceId;

    @Id
    @GeneratedValue
    private Long id;

    public EnumLW(String name) {
        this.name = name;
        this.enumConstants = new ArrayList<>();
        this.constructors = new ArrayList<>();
        this.methods = new ArrayList<>();
        this.fields = new ArrayList<>();
    }

    public EnumLW(String name, List<EnumConstant> enumConstants,
                  List<Constructor> constructors, List<Method> methods,
                  List<Field> fields, Body body) {
        this.name = name;
        this.enumConstants = enumConstants;
        this.constructors = constructors;
        this.methods = methods;
        this.fields = fields;
        this.body = body;
    }

    protected EnumLW() {
        this.enumConstants = new ArrayList<>();
        this.constructors = new ArrayList<>();
        this.methods = new ArrayList<>();
        this.fields = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void addEnumConstant(EnumConstant enumConstant) {
        enumConstants.add(enumConstant);
    }

    public void addConstructor(Constructor constructor) {
        constructors.add(constructor);
    }

    public void addMethod(Method method) {
        methods.add(method);
    }

    public List<EnumConstant> getEnumConstants() {
        return enumConstants;
    }

    public List<Constructor> getConstructors() {
        return constructors;
    }

    public List<Method> getMethods() {
        return methods;
    }

    public Body getBody() {
        return body;
    }

    public List<Field> getFields() {
        return fields;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEnumConstants(List<EnumConstant> enumConstants) {
        this.enumConstants = enumConstants;
    }

    public void setConstructors(List<Constructor> constructors) {
        this.constructors = constructors;
    }

    public void setMethods(List<Method> methods) {
        this.methods = methods;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    @Override
    public Optional<EnumLW> asEnumLW() {
        return Optional.of(this);
    }
}
