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

    /**
     * This method tries to find the component you are looking for in its children.
     *
     * @param exactName Name of the component which you want to find.
     * @param clazz class of the component you want to find.
     * @param <T> this is automatically inferred from passed clazz.
     * @return returns a Optional containing the component.
     */
    default <T extends ParsedComponent> Optional<T> findInChildren(String exactName, Class<T> clazz) {

        return getChildren()
                .values()
                .stream()
                .map(parsedComponent -> parsedComponent.find(exactName, clazz))
                .filter(Optional::isPresent)
                .findFirst()
                .orElse(Optional.empty());
    }
}
