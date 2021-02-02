package org.java2uml.java2umlapi.umlComponenets;

import com.github.javaparser.resolution.declarations.ResolvedDeclaration;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * <p>
 * This ParsedComponent interface declares common methods for SourceComponent (which is a composite component),
 * ParsedClassOrInterfaceComponent (which is a composite component), ParsedMethodComponent (which is a simple component),
 * ParsedConstructorComponent (which is a simple component), and ParsedFieldComponent (which is a simple component).
 * </p>
 * <p>
 * Optional has been used to avoid the dreaded NullPointerException.
 * </p>
 * <p>
 * For more information on composite design pattern, go to below link:<br>
 * https://refactoring.guru/design-patterns/composite
 * </p>
 *
 * @author kawaiifox
 */
public interface ParsedComponent {
    /**
     * @return returns wrapped Optional<ResolvedDeclaration>.
     */
    default Optional<ResolvedDeclaration> getResolvedDeclaration() {
        return Optional.empty();
    }

    /**
     * @return returns true if the component is a SourceComponent.
     */
    default boolean isSourceComponent() {
        return false;
    }

    /**
     * @return returns true if the component is a ParsedClassOrInterfaceComponent.
     */
    default boolean isParsedClassOrInterfaceComponent() {
        return false;
    }

    /**
     * @return returns true if the component is a ParsedMethodComponent.
     */
    default boolean isParsedMethodComponent() {
        return false;
    }

    /**
     * @return returns true if the component is a ParsedConstructorComponent.
     */
    default boolean isParsedConstructorComponent() {
        return false;
    }

    /**
     * @return returns true if the component is a ParsedFieldComponent.
     */
    default boolean isParseFieldComponent() {
        return false;
    }

    /**
     * @return returns true if the component is a leaf component.
     */
    boolean isLeaf();

    /**
     * @return returns Optional<SourceComponent>.
     */
    default Optional<SourceComponent> asSourceComponent() {
        return Optional.empty();
    }

    /**
     * @return returns Optional<ParsedClassOrInterfaceComponent>.
     */
    default Optional<ParsedClassOrInterfaceComponent> asParsedClassOrInterfaceComponent() {
        return Optional.empty();
    }

    /**
     * @return returns Optional<ParsedMethodComponent>.
     */
    default Optional<ParsedMethodComponent> asParsedMethodComponent() {
        return Optional.empty();
    }

    /**
     * @return returns Optional<ParsedConstructorComponent>.
     */
    default Optional<ParsedConstructorComponent> asParsedConstructorComponent() {
        return Optional.empty();
    }

    /**
     * @return returns Optional<ParsedFieldComponent>.
     */
    default Optional<ParsedFieldComponent> asParsedFieldComponent() {
        return Optional.empty();
    }

    /**
     * @return returns parent of current component.
     */
    Optional<ParsedComponent> getParent();

    /**
     * @return returns children of current component.
     * Empty Optional is returned if current component is a leaf.
     */
    default Optional<Map<String,ParsedComponent>> getChildren() {
        return Optional.empty();
    }

    /**
     * @return Returns name of the component, on which it is called.
     */
    String getName();

}
