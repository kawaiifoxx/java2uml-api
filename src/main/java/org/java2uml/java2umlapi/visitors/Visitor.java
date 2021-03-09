package org.java2uml.java2umlapi.visitors;

import org.java2uml.java2umlapi.umlComponenets.*;

/**
 * <p>
 * Visitor can be used for traversing umlComponent and performing some operations on them.
 * </p>
 *
 * @author kawaiifox
 */
public interface Visitor<R> {
    /**
     * Visits passed component and performs some operation on it.
     *
     * @param sourceComponent sourceComponent representing project.
     * @return Object of type R
     */
    R visit(SourceComponent sourceComponent);

    /**
     * Visits passed component and performs some operation on it.
     *
     * @param parsedClassOrInterfaceComponent parsedClassOrInterfaceComponent representing ClassOrInterface.
     * @return Object of type R
     */
    R visit(ParsedClassOrInterfaceComponent parsedClassOrInterfaceComponent);

    /**
     * Visits passed component and performs some operation on it.
     *
     * @param parsedEnumComponent parsedEnumComponent representing Enum.
     * @return Object of type R
     */
    R visit(ParsedEnumComponent parsedEnumComponent);

    /**
     * Visits passed component and performs some operation on it.
     *
     * @param parsedMethodComponent parsedMethodComponent representing Method.
     * @return Object of type R
     */
    R visit(ParsedMethodComponent parsedMethodComponent);

    /**
     * Visits passed component and performs some operation on it.
     *
     * @param parsedFieldComponent parsedFieldComponent representing Field.
     * @return Object of type R
     */
    R visit(ParsedFieldComponent parsedFieldComponent);

    /**
     * Visits passed component and performs some operation on it.
     *
     * @param parsedConstructorComponent parsedConstructorComponent representing Constructor.
     * @return Object of type R
     */
    R visit(ParsedConstructorComponent parsedConstructorComponent);

    /**
     * Visits passed component and performs some operation on it.
     *
     * @param parsedExternalComponent parsedExternalComponent representing External Class or interface or enum.
     * @return Object of type R
     */
    R visit(ParsedExternalComponent parsedExternalComponent);

    /**
     * Visits passed component and performs some operation on it.
     *
     * @param parsedEnumConstantComponent parsedEnumConstantComponent representing Enum constant.
     * @return Object of type R
     */
    R visit(ParsedEnumConstantComponent parsedEnumConstantComponent);

    /**
     * Visits passed TypeRelation and performs some operation on it.
     */
    R visit(TypeRelation typeRelation);
}
