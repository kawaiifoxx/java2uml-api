package org.java2uml.java2umlapi.parsedComponent;

import com.github.javaparser.resolution.declarations.ResolvedDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedEnumConstantDeclaration;
import org.java2uml.java2umlapi.visitors.Visitor;

import java.util.Optional;

public class ParsedEnumConstantComponent implements ParsedComponent {

    private final String name;
    private final ResolvedEnumConstantDeclaration resolvedDeclaration;
    private final ParsedComponent parent;

    public ParsedEnumConstantComponent(ResolvedEnumConstantDeclaration resolvedDeclaration, ParsedComponent parent) {
        this.name = parent.getName() + "." + resolvedDeclaration.getName();
        this.resolvedDeclaration = resolvedDeclaration;
        this.parent = parent;
    }

    /**
     * @return returns true if the component is a leaf component.
     */
    @Override
    public boolean isLeaf() {
        return true;
    }

    /**
     * @return returns parent of current component.
     */
    @Override
    public Optional<ParsedComponent> getParent() {
        return Optional.of(parent);
    }

    /**
     * @return Returns name of the component, on which it is called.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @return returns wrapped Optional<ResolvedDeclaration>.
     */
    @Override
    public Optional<ResolvedDeclaration> getResolvedDeclaration() {
        return Optional.of(resolvedDeclaration);
    }

    public ResolvedEnumConstantDeclaration getResolvedEnumConstantDeclaration() {
        return resolvedDeclaration;
    }

    /**
     * @return returns true if the current component is a ParsedEnumConstantComponent
     */
    @Override
    public boolean isParsedEnumConstantComponent() {
        return true;
    }

    /**
     * @return returns Optional.empty() if this component is not ParsedEnumConstantComponent
     */
    @Override
    public Optional<ParsedEnumConstantComponent> asParsedEnumConstantComponent() {
        return Optional.of(this);
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
    public String toString() {
        return "ParsedEnumConstantComponent{" +
                "name='" + name + '\'' +
                '}';
    }
}
