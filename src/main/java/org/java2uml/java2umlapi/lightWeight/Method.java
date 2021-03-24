package org.java2uml.java2umlapi.lightWeight;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class Method extends LightWeight {
    @Column(columnDefinition = "varchar(500)")
    private String name;
    @JsonIgnore
    @Column(columnDefinition = "varchar(500)")
    private String packageName;
    @Column(columnDefinition = "varchar(500)")
    private String returnType;
    @Column(columnDefinition = "varchar(500)")
    private String signature;
    @Column(columnDefinition = "varchar(10)")
    private String visibility;
    private boolean isStatic;
    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Param> methodParameters;
    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TypeParam> methodTypeParameters;
    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SpecifiedException> specifiedExceptions;
    @JsonIgnore
    @OneToOne(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Body body;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    private LightWeight parent;


    protected Method() {
    }

    public Method(String name, String signature, String returnType, String visibility) {
        this.name = name;
        this.signature = signature;
        this.visibility = visibility;
        this.returnType = returnType;
        this.isStatic = false;
    }

    public Method(String name, String signature, String returnType, String visibility, boolean isStatic) {
        this.name = name;
        this.returnType = returnType;
        this.signature = signature;
        this.visibility = visibility;
        this.isStatic = isStatic;
    }

    public Method(
            String name, String returnType, String signature,
            String visibility, boolean isStatic,
            List<Param> params, List<TypeParam> typeParams,
            List<SpecifiedException> specifiedExceptions, Body body
    ) {
        this.name = name;
        this.returnType = returnType;
        this.signature = signature;
        this.visibility = visibility;
        this.isStatic = isStatic;
        this.methodParameters = params;
        this.methodTypeParameters = typeParams;
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
        methodParameters.add(param);
    }

    public List<Param> getMethodParameters() {
        return methodParameters;
    }

    public List<TypeParam> getMethodTypeParameters() {
        return methodTypeParameters;
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

    public void setMethodParameters(List<Param> params) {
        this.methodParameters = params;
    }

    public void setMethodTypeParameters(List<TypeParam> typeParams) {
        this.methodTypeParameters = typeParams;
    }

    public void setParent(LightWeight parent) {
        this.parent = parent;
    }

    public String getReturnType() {
        return returnType;
    }

    public Body getBody() {
        return body;
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

    @Override
    public LightWeight getParent() {
        return parent;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
