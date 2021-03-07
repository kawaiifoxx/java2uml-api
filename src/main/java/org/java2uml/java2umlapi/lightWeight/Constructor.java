package org.java2uml.java2umlapi.lightWeight;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * A lightweight data class containing all necessary information for a particular constructor in source files.
 * </p>
 *
 * @author kawaiifox
 */
@Entity
public class Constructor implements LightWeight {
    @Column(columnDefinition = "varchar(500)")
    private String name;
    @Column(columnDefinition = "varchar(500)")
    private String signature;
    @Column(columnDefinition = "varchar(10)")
    private String visibility;
    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Param> params;
    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TypeParam> typeParams;
    @JsonIgnore
    @OneToOne(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Body body;
    private boolean compilerGenerated;
    private Long ownerId;

    @Id
    @GeneratedValue
    private Long id;


    protected Constructor() {
        this.params = new ArrayList<>();
        this.typeParams = new ArrayList<>();
    }

    public Constructor(String name, String signature, String visibility, Body body) {
        this.name = name;
        this.signature = signature;
        this.visibility = visibility;
        this.body = body;
        this.params = new ArrayList<>();
        this.typeParams = new ArrayList<>();
    }

    public Constructor(String name, String signature, String visibility, List<Param> params, List<TypeParam> typeParams, Body body, boolean compilerGenerated) {
        this.name = name;
        this.signature = signature;
        this.visibility = visibility;
        this.params = params;
        this.typeParams = typeParams;
        this.body = body;
        this.compilerGenerated = compilerGenerated;
    }

    public String getName() {
        return name;
    }

    public String getSignature() {
        return signature;
    }

    public String getVisibility() {
        return visibility;
    }

    public void addParam(Param param) {
        params.add(param);
    }

    public List<Param> getParams() {
        return params;
    }

    public Long getId() {
        return id;
    }

    public Body getBody() {
        return body;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public void setParams(List<Param> params) {
        this.params = params;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * if this light weight is Constructor then a Constructor is returned.
     *
     * @return Constructor
     */
    @Override
    public Optional<Constructor> asConstructor() {
        return Optional.of(this);
    }

    public List<TypeParam> getTypeParams() {
        return typeParams;
    }

    public void setTypeParams(List<TypeParam> typeParams) {
        this.typeParams = typeParams;
    }

    public boolean isCompilerGenerated() {
        return compilerGenerated;
    }

    public void setCompilerGenerated(boolean compilerGenerated) {
        this.compilerGenerated = compilerGenerated;
    }
}
