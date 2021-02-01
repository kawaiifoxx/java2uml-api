package org.java2uml.java2umlapi.umlComponenets;

import com.github.javaparser.resolution.declarations.ResolvedDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;

import java.util.List;
import java.util.Optional;

/**
 * <p>
 * This Component interface declares common methods for ParsedSourceComponent (which is a composite component),
 * ParsedClassComponent (which is a composite component), ParsedMethodComponent (which is a simple component),
 * ParsedConstructorComponent (which is a simple component), and ParsedFieldComponent (which is a simple component).
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
     * @return returns Optional<SourceComponent>.
     */
    default Optional<SourceComponent> asSourceComponent() {
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
    Optional<List<ParsedComponent>> getChildren();

}
