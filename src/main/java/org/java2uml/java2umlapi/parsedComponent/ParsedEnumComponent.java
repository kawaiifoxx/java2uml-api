package org.java2uml.java2umlapi.parsedComponent;

import com.github.javaparser.resolution.declarations.ResolvedDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedEnumDeclaration;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.java2uml.java2umlapi.visitors.Visitor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ParsedEnumComponent implements ParsedCompositeComponent {

    private final ResolvedEnumDeclaration resolvedEnumDeclaration;
    private final ParsedComponent parent;
    private final String name;
    private final String packageName;
    private final HashMap<String, ParsedComponent> children;

    public ParsedEnumComponent(ResolvedEnumDeclaration resolvedEnumDeclaration, ParsedComponent parent) {
        this.resolvedEnumDeclaration = resolvedEnumDeclaration;
        this.parent = parent;
        this.name = resolvedEnumDeclaration.getQualifiedName();
        this.children = new HashMap<>();
        this.packageName = resolvedEnumDeclaration.getPackageName();
    }

    /**
     * @return returns true if the component is a leaf component.
     */
    @Override
    public boolean isLeaf() {
        return false;
    }

    /**
     * @return returns parent of current component.
     */
    @Override
    public Optional<ParsedComponent> getParent() {
        return Optional.of(parent);
    }

    /**
     * @return returns wrapped Optional<ResolvedDeclaration>.
     */
    @Override
    public Optional<ResolvedDeclaration> getResolvedDeclaration() {
        return Optional.of(resolvedEnumDeclaration);
    }

    /**
     * @return Returns name of the component, on which it is called.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @return package name of the type.
     */
    @Override
    public String getPackageName() {
        return packageName;
    }

    /**
     * @return returns children of current component.
     * Empty Optional is returned if current component is a leaf.
     */
    @Override
    public Map<String, ParsedComponent> getChildren() {
        return children;
    }

    /**
     * Add a child to the this component, such as field, constructor or method.
     *
     * @param parsedComponent component representing child could be a ParsedEnumConstantComponent, field, constructor,
     *                        method or any other component which can be contained in a Enum.
     */
    public void addChild(ParsedComponent parsedComponent) {
        children.put(parsedComponent.getName(), parsedComponent);
    }

    /**
     * @return returns true if the current component is a ParsedEnumComponent
     */
    @Override
    public boolean isParsedEnumComponent() {
        return true;
    }

    /**
     * @return returns Optional.empty() if this component is not ParsedEnumComponent
     */
    @Override
    public Optional<ParsedEnumComponent> asParsedEnumComponent() {
        return Optional.of(this);
    }

    /**
     * Finds and returns the reference for ParsedComponent for which name and class is provided.
     *
     * @param exactName Name of the component to be found.
     * @param clazz     class of the component.
     * @return ParsedComponent if present, empty optional otherwise.
     */
    @Override
    public <T extends ParsedComponent> Optional<T> find(String exactName, Class<T> clazz) {

        if (exactName.equals(name) && clazz.equals(this.getClass())) {
            //noinspection unchecked
            return Optional.of((T) this);
        }

        return findInChildren(exactName, clazz);
    }

    /**
     * Accepts a visitor and returns whatever is returned by the visitor.
     *
     * @param v v is the Visitor
     * @return data extracted by visitor.
     */
    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ParsedEnumComponent)) return false;

        ParsedEnumComponent that = (ParsedEnumComponent) o;

        return new EqualsBuilder()
                .append(getName(), that.getName())
                .append(getPackageName(), that.getPackageName())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getName())
                .append(getPackageName())
                .toHashCode();
    }

    @Override
    public String toString() {
        return "ParsedEnumComponent{" +
                "name='" + name + '\'' +
                '}';
    }
}
