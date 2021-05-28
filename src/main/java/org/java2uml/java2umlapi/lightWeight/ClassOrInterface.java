package org.java2uml.java2umlapi.lightWeight;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
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
    @Column(columnDefinition = "varchar(1000)")
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

    /**
     * <p>
     * Builder for building a new instance of {@link ClassOrInterface}. This builder provides methods for,
     * customizing {@link ClassOrInterface} instance to your needs.
     * </p>
     *
     * @author kawaiifox
     */
    public static class Builder {
        private final ClassOrInterface classOrInterface;
        /* Initially Object has not been built. */
        private boolean built = false;

        /**
         * Initializes Builder with a new instance of {@link ClassOrInterface}
         */
        public Builder() {
            classOrInterface = new ClassOrInterface();
        }

        /**
         * Adds a class name to {@link ClassOrInterface} instance.
         *
         * @param name Fully qualified class name.
         * @return this {@link Builder}
         */
        public Builder withName(String name) {
            classOrInterface.name = name;
            return this;
        }

        /**
         * Adds a package name to {@link ClassOrInterface} instance.
         *
         * @param pkgName name of the package to which this {@link ClassOrInterface} instance belongs to.
         * @return this {@link Builder}
         */
        public Builder withPackageName(String pkgName) {
            classOrInterface.packageName = pkgName;
            return this;
        }

        /**
         * Adds all the {@link Constructor} belonging to this {@link ClassOrInterface} instance.
         *
         * @param classConstructors {@link List} of {@link Constructor}s.
         * @return this {@link Builder}.
         */
        public Builder withClassConstructors(List<Constructor> classConstructors) {
            classOrInterface.classConstructors = classConstructors;
            return this;
        }

        /**
         * Adds all the {@link Method} belonging to this {@link ClassOrInterface} instance.
         *
         * @param methods {@link List} of {@link Method}s.
         * @return this {@link Builder}.
         */
        public Builder withMethods(List<Method> methods) {
            classOrInterface.classOrInterfaceMethods = methods;
            return this;
        }

        /**
         * Adds all {@link Field} belonging to this {@link ClassOrInterface} instance.
         *
         * @param fields {@link List} of {@link Field}s.
         * @return this builder.
         */
        public Builder withFields(List<Field> fields) {
            classOrInterface.classFields = fields;
            return this;
        }

        /**
         * Adds all the {@link TypeParam} to belonging to this {@link ClassOrInterface} instance.
         *
         * @param typeParameters {@link List} of {@link TypeParam}
         * @return this {@link Builder}
         */
        public Builder withTypeParameters(List<TypeParam> typeParameters) {
            classOrInterface.classOrInterfaceTypeParameters = typeParameters;
            return this;
        }

        /**
         * Is this {@link ClassOrInterface} instance is class?
         *
         * @param isClass boolean describing whether this instance is class or not.
         * @return this {@link Builder}
         */
        public Builder withIsClass(boolean isClass) {
            classOrInterface.isClass = isClass;
            return this;
        }

        /**
         * Is this {@link ClassOrInterface} external to the project?
         *
         * @param isExternal boolean describing whether this instance is external to the project.
         * @return this {@link Builder}
         */
        public Builder withIsExternal(boolean isExternal) {
            classOrInterface.isExternal = isExternal;
            return this;
        }

        /**
         * Adds {@link Body} of this {@link ClassOrInterface} instance.
         *
         * @param body {@link Body} instance belonging to this {@link ClassOrInterface} instance.
         * @return this {@link Builder}
         */
        public Builder withBody(Body body) {
            classOrInterface.body = body;
            return this;
        }

        /**
         * Builds an instance of {@link ClassOrInterface} with all the provided properties.
         *
         * @return {@link ClassOrInterface} Instance.
         * @throws UnsupportedOperationException if <code>build()</code> has already been called before.
         * i.e an instance has already been built using this builder once. If you want another instance
         * of {@link ClassOrInterface}, create new instance of {@link Builder}
         */
        public ClassOrInterface build() {
            if (!built) {
                built = true;
                return classOrInterface;
            }
            throw new UnsupportedOperationException("Build method can only be called once.");
        }
    }

    @Override
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
                Objects.equals(getClassOrInterfaceTypeParameters(), that.getClassOrInterfaceTypeParameters());
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
