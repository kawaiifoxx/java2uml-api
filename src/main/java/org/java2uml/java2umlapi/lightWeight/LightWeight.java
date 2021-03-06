package org.java2uml.java2umlapi.lightWeight;

import java.util.Optional;

public interface LightWeight {
    /**
     * if this light weight is source then a source is returned.
     *
     * @return source
     */
    default Optional<Source> asSource() {
        return Optional.empty();
    }

    /**
     * if this light weight is classOrInterface then a classOrInterface is returned.
     *
     * @return classOrInterface
     */
    default Optional<ClassOrInterface> asClassOrInterface() {
        return Optional.empty();
    }

    /**
     * if this light weight is EnumLW then a EnumLW is returned.
     *
     * @return EnumLW
     */
    default Optional<EnumLW> asEnumLW() {
        return Optional.empty();
    }

    /**
     * if this light weight is Constructor then a Constructor is returned.
     *
     * @return Constructor
     */
    default Optional<Constructor> asConstructor() {
        return Optional.empty();
    }

    /**
     * if this light weight is Field then a Field is returned.
     *
     * @return Field
     */
    default Optional<Field> asField() {
        return Optional.empty();
    }

    /**
     * if this light weight is Method then a Method is returned.
     *
     * @return Method
     */
    default Optional<Method> asMethod() {
        return Optional.empty();
    }

    /**
     * if this light weight is EnumConstant then a EnumConstant is returned.
     *
     * @return EnumConstant
     */
    default Optional<EnumConstant> asEnumConstant() {
        return Optional.empty();
    }
}
