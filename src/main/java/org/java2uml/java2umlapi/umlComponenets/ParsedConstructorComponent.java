package org.java2uml.java2umlapi.umlComponenets;

import com.github.javaparser.resolution.declarations.ResolvedConstructorDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedDeclaration;
import org.java2uml.java2umlapi.util.umlSymbols.UMLModifier;
import org.java2uml.java2umlapi.util.umlSymbols.VisibilityModifierSymbol;
import org.java2uml.java2umlapi.visitors.Visitor;

import java.util.Optional;

/**
 * <p>
 *     A leaf component, representing a constructor, this is used in this composite tree to generate uml for constructors.
 * </p>
 *
 * @author kawaiifox
 */
public class ParsedConstructorComponent implements ParsedComponent {
    private final ResolvedConstructorDeclaration resolvedDeclaration;
    private final ParsedComponent parent;
    private final String name;

    /**
     * Initializes ParsedConstructorComponent.
     * @param parent Parent of this component.
     * @param resolvedDeclaration resolvedConstructorDeclaration is type solved constructor declaration
     *                            retrieved from resolvedReferenceTypeDeclaration.
     */
    public ParsedConstructorComponent(ParsedComponent parent, ResolvedConstructorDeclaration resolvedDeclaration) {
        this.resolvedDeclaration = resolvedDeclaration;
        this.parent = parent;
        this.name = resolvedDeclaration.getQualifiedSignature();
    }

    @Override
    public Optional<ResolvedDeclaration> getResolvedDeclaration() {
        return Optional.of(resolvedDeclaration);
    }

    /**
     * @return ResolvedConstructorDeclaration belonging to this component.
     */
    public ResolvedConstructorDeclaration getResolvedConstructorDeclaration() {
        return resolvedDeclaration;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public boolean isParsedConstructorComponent() {
        return true;
    }

    @Override
    public Optional<ParsedConstructorComponent> asParsedConstructorComponent() {
        return Optional.of(this);
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
     * @return returns the uml form of this component.
     */
    @Override
    public String toUML() {
        return VisibilityModifierSymbol.of(resolvedDeclaration.accessSpecifier().asString()) + " "
                + UMLModifier.METHOD + " [Constructor] " + resolvedDeclaration.getSignature();
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
        return "ParsedConstructorComponent{" +
                "name='" + name + '\'' +
                '}';
    }
}
