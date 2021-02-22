package org.java2uml.java2umlapi.umlComponenets;

import java.util.Optional;


/**
 * <p>
 * This is a composite component, which have another component as its children, it is mainly
 * used for generating uml syntax for composite components.
 * </p>
 *
 * @author kawaiifoxx
 */
public interface ParsedCompositeComponent extends ParsedComponent {

    /**
     * @return Returns name of the component, on which it is called.
     */
    @Override
    String getName();

    /**
     * @return returns true if the component is a leaf component.
     */
    @Override
    default boolean isLeaf() {
        return false;
    }

    /**
     * @return returns ParsedCompositeComponent if current component is a ParsedCompositeComponent.
     */
    @Override
    default Optional<ParsedCompositeComponent> asParsedCompositeComponent() {
        return Optional.of(this);
    }

    default <T extends ParsedComponent> Optional<T> findInChildren(String exactName, Class<T> clazz) {
        var children = getChildren();

        for (var child : children.entrySet()) {
            var result = child.getValue().find(exactName, clazz);

            if (result.isPresent()) {
                return result;
            }
        }

        return Optional.empty();
    }
}
