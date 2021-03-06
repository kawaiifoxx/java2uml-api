package org.java2uml.java2umlapi.umlComponenets;

import com.github.javaparser.resolution.declarations.ResolvedDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedTypeDeclaration;
import org.java2uml.java2umlapi.visitors.Visitor;

import java.util.Optional;

import static org.java2uml.java2umlapi.util.umlSymbols.TypeDeclarationSymbol.getTypeDeclarationSymbol;

/**
 * <p>
 * Any classOrInterfaceDeclaration or annotationDeclaration or enumDeclaration
 * which does not belong to the project being parsed and is dependency of the project being parsed is stored in
 * this component.
 * </p>
 *
 * @author kawaiifox
 */
public class ParsedExternalComponent implements ParsedCompositeComponent {

    private final ResolvedTypeDeclaration resolvedTypeDeclaration;

    private String typeDeclaration;
    private final String name;

    /**
     * Initializes ParsedExternalComponent with a resolvedTypeDeclaration.
     * @param resolvedTypeDeclaration it is resolvedTypeDeclaration which is obtained after type solving.
     *                                (Provided by parser in our case.)
     */
    public ParsedExternalComponent(ResolvedTypeDeclaration resolvedTypeDeclaration) {
        this.resolvedTypeDeclaration = resolvedTypeDeclaration;
        this.name = resolvedTypeDeclaration.getQualifiedName();
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public boolean isParsedExternalAncestor() {
        return true;
    }

    @Override
    public Optional<ParsedComponent> getParent() {
        return Optional.empty();
    }

    @Override
    public Optional<ResolvedDeclaration> getResolvedDeclaration() {
        return Optional.of(resolvedTypeDeclaration);
    }

    /**
     * @return returns Optional.empty() if this component is not ParsedExternalComponent
     */
    @Override
    public Optional<ParsedExternalComponent> asParsedExternalComponent() {
        return Optional.of(this);
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * @return Returns uml form of this component.
     */
    @Override
    public String toUML() {
        if (typeDeclaration == null) {
            typeDeclaration = getTypeDeclarationSymbol(resolvedTypeDeclaration);
        }

        return typeDeclaration + " {\n}";
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
        return "ParsedExternalComponent{" +
                "name='" + name + '\'' +
                '}';
    }
}
