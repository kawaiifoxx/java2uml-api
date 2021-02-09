package org.java2uml.java2umlapi.umlComponenets;

import com.github.javaparser.resolution.declarations.ResolvedDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedTypeDeclaration;

import java.util.Optional;

import static org.java2uml.java2umlapi.util.umlSymbols.TypeDeclarationSymbol.getTypeDeclarationSymbol;

/**
 * <p>
 * This is a leaf component, any classOrInterfaceDeclaration or annotationDeclaration or enumDeclaration
 * which does not belong to the project being parsed and is dependency of the project being parsed is stored in
 * this component.
 * </p>
 *
 * @author kawaiifox
 */
public class ParsedExternalComponent implements ParsedComponent {

    private ResolvedTypeDeclaration resolvedTypeDeclaration;

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
}
