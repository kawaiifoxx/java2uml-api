package org.java2uml.java2umlapi.lightWeight;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * A lightweight data class containing all necessary information for a particular constructor in a source file.
 * </p>
 *
 * @author kawaiifox
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Constructor extends LightWeight {
    @Column(columnDefinition = "varchar(500)")
    private String name;
    @Column(columnDefinition = "varchar(500)")
    private String signature;
    @Column(columnDefinition = "varchar(10)")
    private String visibility;
    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Param> constructorParameters;
    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TypeParam> constructorTypeParameters;
    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SpecifiedException> constructorSpecifiedExceptions;
    @JsonIgnore
    @OneToOne(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Body body;
    private boolean compilerGenerated;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private LightWeight parent;

    @Id
    @GeneratedValue
    private Long id;


    protected Constructor() {
        this.constructorParameters = new ArrayList<>();
        this.constructorTypeParameters = new ArrayList<>();
    }

    public Constructor(String name, String signature, String visibility, Body body) {
        this.name = name;
        this.signature = signature;
        this.visibility = visibility;
        this.body = body;
        this.constructorParameters = new ArrayList<>();
        this.constructorTypeParameters = new ArrayList<>();
    }

    public Constructor(String name, String signature, String visibility, boolean compilerGenerated) {
        this.name = name;
        this.signature = signature;
        this.visibility = visibility;
        this.compilerGenerated = compilerGenerated;
    }

    public Constructor(
            String name, String signature, String visibility,
            List<Param> params, List<TypeParam> constructorTypeParameters,
            Body body, boolean compilerGenerated
    ) {
        this.name = name;
        this.signature = signature;
        this.visibility = visibility;
        this.constructorParameters = params;
        this.constructorTypeParameters = constructorTypeParameters;
        this.body = body;
        this.compilerGenerated = compilerGenerated;
    }

    @Override
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
        constructorParameters.add(param);
    }

    public List<Param> getConstructorParameters() {
        return constructorParameters;
    }

    public Long getId() {
        return id;
    }

    public Body getBody() {
        return body;
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

    public void setParent(LightWeight parent) {
        this.parent = parent;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public void setConstructorTypeParameters(List<TypeParam> typeParams) {
        this.constructorTypeParameters = typeParams;
    }

    public void setConstructorParameters(List<Param> params) {
        this.constructorParameters = params;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public void setConstructorSpecifiedExceptions(List<SpecifiedException> constructorSpecifiedExceptions) {
        this.constructorSpecifiedExceptions = constructorSpecifiedExceptions;
    }

    public void setCompilerGenerated(boolean compilerGenerated) {
        this.compilerGenerated = compilerGenerated;
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

    public List<TypeParam> getConstructorTypeParameters() {
        return constructorTypeParameters;
    }

    public List<SpecifiedException> getConstructorSpecifiedExceptions() {
        return constructorSpecifiedExceptions;
    }

    public boolean isCompilerGenerated() {
        return compilerGenerated;
    }

    @Override
    public LightWeight getParent() {
        return parent;
    }
}
