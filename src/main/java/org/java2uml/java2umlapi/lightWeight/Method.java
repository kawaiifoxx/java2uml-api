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

    /**
     * <p>
     * A Builder for constructing {@link Method}s with all of it's attributes.
     * </p>
     *
     * @author kawaiifox
     */
    public static class Builder {
        private final Method method;

        public Builder() {
            this.method = new Method();
        }

        /**
         * Add name.
         *
         * @param name {@link Method} name
         * @return {@link Builder}
         */
        public Builder withName(String name) {
            method.name = name;
            return this;
        }

        /**
         * Add  Package Name.
         *
         * @param packageName {@link Method} package name
         * @return {@link Builder}
         */
        public Builder withPackageName(String packageName) {
            method.packageName = packageName;
            return this;
        }

        /**
         * Add visibility.
         *
         * @param visibility {@link Method}'s visibility
         * @return {@link Builder}
         */
        public Builder withVisibility(String visibility) {
            method.visibility = visibility;
            return this;
        }

        /**
         * Add return type of the method.
         *
         * @param returnType {@link Method}'s return type.
         * @return {@link Builder}
         */
        public Builder withReturnType(String returnType) {
            method.returnType = returnType;
            return this;
        }

        /**
         * Add signature of method.
         *
         * @param signature {@link Method} signature.
         * @return Builder
         */
        public Builder withSignature(String signature) {
            method.signature = signature;
            return this;
        }

        /**
         * Add whether the method is static.
         *
         * @param isStatic true if static else false.
         * @return {@link Builder}
         */
        public Builder withStatic(boolean isStatic) {
            method.isStatic = isStatic;
            return this;
        }

        /**
         * Add parameters of the method.
         *
         * @param params {@link List} of {@link Param}
         * @return {@link Builder}
         */
        public Builder withParameters(List<Param> params) {
            method.methodParameters = params;
            return this;
        }

        /**
         * Add type parameters of the {@link Method}
         *
         * @param typeParams {@link List} of {@link TypeParam}
         * @return {@link Builder}
         */
        public Builder withTypeParameters(List<TypeParam> typeParams) {
            method.methodTypeParameters = typeParams;
            return this;
        }

        /**
         * Add specified exceptions for the {@link Method}
         *
         * @param specifiedExceptions {@link List} of {@link SpecifiedException}
         * @return {@link Builder}
         */
        public Builder withSpecifiedExceptions(List<SpecifiedException> specifiedExceptions) {
            method.specifiedExceptions = specifiedExceptions;
            return this;
        }

        /**
         * Add body of the {@link Method}
         *
         * @param body {@link Body} of belonging to the {@link Method}
         * @return {@link Builder}
         */
        public Builder withBody(Body body) {
            method.body = body;
            return this;
        }

        /**
         * Add parent of the {@link Method}
         *
         * @param parent Parent of this {@link Method}
         * @return {@link Builder}
         */
        public Builder withParent(LightWeight parent) {
            method.parent = parent;
            return this;
        }

        /**
         * Constructs the method and returns it.
         * @return {@link Method} with all the added fields.
         * @throws IllegalStateException if name, signature or return type is not added.
         */
        public Method build() {
            if (method.name == null || method.signature == null || method.returnType == null) {
                throw new IllegalStateException("A method should have name, return type and signature for building it.");
            }

            return method;
        }
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

    public List<Param> getMethodParameters() {
        return methodParameters;
    }

    @SuppressWarnings("unused")
    public List<TypeParam> getMethodTypeParameters() {
        return methodTypeParameters;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @SuppressWarnings("unused")
    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    @SuppressWarnings("unused")
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

    @SuppressWarnings("unused")
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
