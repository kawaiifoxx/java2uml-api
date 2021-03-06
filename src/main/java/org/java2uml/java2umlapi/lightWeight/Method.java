package org.java2uml.java2umlapi.lightWeight;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
/**
 * <p>
 * An Entity Class representing method in source files.
 * </p>
 *
 * @author kawaiifox
 */
@Entity
public class Method implements LightWeight {
    @Column(columnDefinition = "varchar(500)")
    private String name;
    @Column(columnDefinition = "varchar(500)")
    private String returnType;
    @Column(columnDefinition = "varchar(500)")
    private String signature;
    @Column(columnDefinition = "varchar(10)")
    private String visibility;
    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Param> params;
    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TypeParam> typeParams;
    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SpecifiedException> specifiedExceptions;
    @JsonIgnore
    @OneToOne(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Body body;
    private Long ownerId;

    @Id
    @GeneratedValue
    private Long id;


    protected Method() {
        this.params = new ArrayList<>();
        this.typeParams = new ArrayList<>();
        this.specifiedExceptions = new ArrayList<>();
    }

    public Method(String name, String signature, String returnType,String visibility) {
        this.name = name;
        this.signature = signature;
        this.visibility = visibility;
        this.returnType = returnType;
        this.params = new ArrayList<>();
        this.typeParams = new ArrayList<>();
        this.specifiedExceptions = new ArrayList<>();
    }

    public Method(String name, String returnType, String signature,
                  String visibility, List<Param> params, List<TypeParam> typeParams,
                  List<SpecifiedException> specifiedExceptions, Body body) {
        this.name = name;
        this.returnType = returnType;
        this.signature = signature;
        this.visibility = visibility;
        this.params = params;
        this.typeParams = typeParams;
        this.specifiedExceptions = specifiedExceptions;
        this.body = body;
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

    public List<TypeParam> getTypeParams() {
        return typeParams;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public void setParams(List<Param> params) {
        this.params = params;
    }

    public void setTypeParams(List<TypeParam> typeParams) {
        this.typeParams = typeParams;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getReturnType() {
        return returnType;
    }

    public Body getBody() {
        return body;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    /**
     * if this light weight is Method then a Method is returned.
     *
     * @return Method
     */
    @Override
    public Optional<Method> asMethod() {
        return Optional.of(this);
    }

    public List<SpecifiedException> getSpecifiedExceptions() {
        return specifiedExceptions;
    }

    public void setSpecifiedExceptions(List<SpecifiedException> specifiedExceptions) {
        this.specifiedExceptions = specifiedExceptions;
    }
}
