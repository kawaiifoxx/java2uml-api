package org.java2uml.java2umlapi.lightWeight;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Optional;

@Entity
public abstract class LightWeight {
    @Id
    @GeneratedValue
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    /**
     * @param parent to of this {@link LightWeight}
     */
    public void setParent(LightWeight parent) {
        throw new UnsupportedOperationException("This lightweight does not have a parent.");
    }


    /**
     * @return parent of this lightWeight.
     */
    @JsonIgnore
    public LightWeight getParent() {
        throw new UnsupportedOperationException("Parent of this lightWeight does not exist.");
    }

    /**
     * @return Name of this lightWeight.
     * @throws UnsupportedOperationException if called on {@link Body} or {@link Source}
     */
    abstract public String getName();

    /**
     * if this light weight is source then a source is returned.
     *
     * @return source
     */
    public Optional<Source> asSource() {
        return Optional.empty();
    }

    /**
     * if this light weight is classOrInterface then a classOrInterface is returned.
     *
     * @return classOrInterface
     */
    public Optional<ClassOrInterface> asClassOrInterface() {
        return Optional.empty();
    }

    /**
     * if this light weight is EnumLW then a EnumLW is returned.
     *
     * @return EnumLW
     */
    public Optional<EnumLW> asEnumLW() {
        return Optional.empty();
    }

    /**
     * if this light weight is Constructor then a Constructor is returned.
     *
     * @return Constructor
     */
    public Optional<Constructor> asConstructor() {
        return Optional.empty();
    }

    /**
     * if this light weight is Field then a Field is returned.
     *
     * @return Field
     */
   public Optional<Field> asField() {
        return Optional.empty();
    }

    /**
     * if this light weight is Method then a Method is returned.
     *
     * @return Method
     */
    public Optional<Method> asMethod() {
        return Optional.empty();
    }

    /**
     * if this light weight is EnumConstant then a EnumConstant is returned.
     *
     * @return EnumConstant
     */
    public Optional<EnumConstant> asEnumConstant() {
        return Optional.empty();
    }
}
