package org.java2uml.java2umlapi.umlComponenets;

import com.github.javaparser.resolution.declarations.ResolvedDeclaration;
import org.java2uml.java2umlapi.visitors.Visitor;

import java.util.HashMap;
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
     * Finds and returns the reference for ParsedComponent for which name and class is provided.
     *
     * @param exactName Name of the component to be found.
     * @param clazz     class of the component.
     * @return ParsedComponent if present, empty optional otherwise.
     */
    default <T extends ParsedComponent> Optional<T> find(String exactName, Class<T> clazz) {
        if (exactName.equals(getName()) && clazz.equals(this.getClass())) {
            //noinspection unchecked
            return Optional.of((T) this);
        }

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
     * @return returns true if the component is a ParsedExternalAncestor.
     */
    default boolean isParsedExternalAncestor() {
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
    default boolean isParsedFieldComponent() {
        return false;
    }

    /**
     * @return returns true if the current component is a ParsedEnumComponent
     */
    default boolean isParsedEnumComponent() {
        return false;
    }

    /**
     * @return returns true if the current component is a ParsedEnumConstantComponent
     */
    default boolean isParsedEnumConstantComponent() {
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
     * @return returns Optional.empty() if this component is not ParsedExternalComponent
     */
    default Optional<ParsedExternalComponent> asParsedExternalComponent() {
        return Optional.empty();
    }

    /**
     * @return returns Optional.empty() if this component is not ParsedEnumComponent
     */
    default Optional<ParsedEnumComponent> asParsedEnumComponent() {
        return Optional.empty();
    }

    /**
     * @return returns Optional.empty() if this component is not ParsedEnumConstantComponent
     */
    default Optional<ParsedEnumConstantComponent> asParsedEnumConstantComponent() {
        return Optional.empty();
    }

    /**
     * @return returns ParsedCompositeComponent if current component is a ParsedCompositeComponent.
     */
    default Optional<ParsedCompositeComponent> asParsedCompositeComponent() {
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
    default Map<String, ParsedComponent> getChildren() {
        return new HashMap<>();
    }

    /**
     * Accepts a visitor and returns whatever is returned by the visitor.
     * @param v v is the Visitor
     * @param <T> Type of data that should be extracted.
     * @return data extracted by visitor.
     */
    <T> T accept(Visitor<T> v);

    /**
     * @return Returns name of the component, on which it is called.
     */
    String getName();

    /**
     * @return Returns generated UML code.
     */
    String toUML();
}
