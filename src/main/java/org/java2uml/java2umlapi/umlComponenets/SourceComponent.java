package org.java2uml.java2umlapi.umlComponenets;

import com.github.javaparser.resolution.declarations.ResolvedDeclaration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * A composite component representing whole java src code, this pattern is meant to be the
 * root of the whole tree.
 * </p>
 *
 * @author kawaiifox
 */
public class SourceComponent implements ParsedComponent {

    private List<ParsedComponent> children;
    private final List<ResolvedDeclaration> allParsedTypes;
    private List<TypeRelation> allRelations;

    public SourceComponent(List<ResolvedDeclaration> allParsedTypes) {
        this.allParsedTypes = allParsedTypes;
        this.children = new ArrayList<>();
        this.allRelations = new ArrayList<>();
    }

    @Override
    public boolean isSourceComponent() {
        return true;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public Optional<SourceComponent> asSourceComponent() {
        return Optional.of(this);
    }

    public List<ResolvedDeclaration> getAllParsedTypes() {
        return allParsedTypes;
    }

    @Override
    public Optional<ParsedComponent> getParent() {
        return Optional.empty();
    }

    @Override
    public Optional<List<ParsedComponent>> getChildren() {
        if (children == null)
            return Optional.empty();
        return Optional.of(children);
    }

    private void generateTreeFromAllParsedTypes(ResolvedDeclaration resolvedDeclaration, ParsedComponent parsedComponent) {

        if (resolvedDeclaration.isType()) {
            var typeDeclaration = resolvedDeclaration.asType().asReferenceType();
            var classOrInterfaceComponent = parsedComponent;
        } else if(resolvedDeclaration.isField()) {

        } else if(resolvedDeclaration.isMethod()) {

        }
    }

    @Override
    public String toString() {
        return "to be implemented";
    }
}
