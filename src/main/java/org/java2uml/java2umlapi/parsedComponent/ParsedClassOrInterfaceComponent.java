package org.java2uml.java2umlapi.parsedComponent;

import com.github.javaparser.resolution.declarations.ResolvedDeclaration;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.java2uml.java2umlapi.visitors.Visitor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * <p>
 * A composite Component, containing other classes or interfaces, or methods or fields as children.
 * </p>
 *
 * @author kawaiifox
 */
public class ParsedClassOrInterfaceComponent implements ParsedCompositeComponent {

    private final ResolvedDeclaration resolvedDeclaration;
    private final ParsedComponent parent;
    private final String name;
    private final String packageName;
    private final boolean isClass;
    private final Map<String, ParsedComponent> children;

    /**
     * Initializes ParsedClassOrInterfaceComponent with resolvedDeclaration and reference to parent.
     *
     * @param resolvedDeclaration ResolvedDeclaration received after typeSolving using a symbol resolver.
     * @param parent              parent of this component.
     */
    public ParsedClassOrInterfaceComponent(ResolvedDeclaration resolvedDeclaration, ParsedComponent parent) {
        this.resolvedDeclaration = resolvedDeclaration;
        this.parent = parent;
        this.name = resolvedDeclaration.asType().getQualifiedName();
        this.packageName = resolvedDeclaration.asType().getPackageName();
        this.children = new HashMap<>();
        this.isClass = resolvedDeclaration.asType().isClass();
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public boolean isParsedClassOrInterfaceComponent() {
        return true;
    }

    @Override
    public Optional<ParsedClassOrInterfaceComponent> asParsedClassOrInterfaceComponent() {
        return Optional.of(this);
    }

    @Override
    public Optional<ResolvedDeclaration> getResolvedDeclaration() {
        return Optional.of(resolvedDeclaration);
    }

    @Override
    public Optional<ParsedComponent> getParent() {
        return Optional.of(parent);
    }

    @Override
    public Map<String, ParsedComponent> getChildren() {
        return children;
    }

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
     * Add a child to the this component, such as field, constructor or method.
     *
     * @param parsedComponent component representing child could be field, constructor,
     *                        method or any other component which can be contained in a Class or Interface.
     */
    public void addChild(ParsedComponent parsedComponent) {
        children.put(parsedComponent.getName(), parsedComponent);
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

    /**
     * @return true if parsedClassOrInterfaceComponent is class.
     */
    public boolean isClass() {
        return isClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ParsedClassOrInterfaceComponent)) return false;

        ParsedClassOrInterfaceComponent that = (ParsedClassOrInterfaceComponent) o;

        return new EqualsBuilder()
                .append(isClass(), that.isClass())
                .append(getParent(), that.getParent())
                .append(getName(), that.getName())
                .append(getPackageName(), that.getPackageName()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getName())
                .append(getPackageName())
                .append(isClass()).toHashCode();
    }

    @Override
    public String toString() {
        return "ParsedClassOrInterfaceComponent{" +
                "name='" + name + '\'' +
                '}';
    }
}
