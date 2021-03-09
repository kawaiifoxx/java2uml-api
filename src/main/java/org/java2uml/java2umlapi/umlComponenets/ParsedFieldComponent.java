package org.java2uml.java2umlapi.umlComponenets;

import com.github.javaparser.resolution.declarations.ResolvedDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import org.java2uml.java2umlapi.visitors.Visitor;

import java.util.Optional;

/**
 * <p>
 * Leaf component representing FieldDeclarations in java source code.
 * </p>
 *
 * @author kawaiifox
 */
public class ParsedFieldComponent implements ParsedComponent {
    private final ParsedComponent parent;
    private final ResolvedFieldDeclaration resolvedDeclaration;
    private final String name;

    /**
     * Initializes ParsedFieldComponent.
     *
     * @param parent              Parent of this component.
     * @param resolvedDeclaration resolvedFieldDeclaration is type solved field declaration
     *                            retrieved from resolvedReferenceTypeDeclaration.
     */
    public ParsedFieldComponent(ParsedComponent parent, ResolvedFieldDeclaration resolvedDeclaration) {
        this.parent = parent;
        this.resolvedDeclaration = resolvedDeclaration;
        this.name = parent.getName() + "." + resolvedDeclaration.getName();
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public boolean isParsedFieldComponent() {
        return true;
    }

    @Override
    public Optional<ParsedFieldComponent> asParsedFieldComponent() {
        return Optional.of(this);
    }

    @Override
    public Optional<ResolvedDeclaration> getResolvedDeclaration() {
        return Optional.of(resolvedDeclaration);
    }

    /**
     * @return a resolvedFieldDeclaration belonging to this component.
     */
    public ResolvedFieldDeclaration getResolvedFieldDeclaration() {
        return resolvedDeclaration;
    }

    @Override
    public Optional<ParsedComponent> getParent() {
        return Optional.of(parent);
    }

    @Override
    public String getName() {
        return name;
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
        return "ParsedFieldComponent{" +
                "name='" + name + '\'' +
                '}';
    }
}
