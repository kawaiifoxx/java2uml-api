package org.java2uml.java2umlapi.umlComponenets;

import com.github.javaparser.resolution.declarations.ResolvedDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedEnumDeclaration;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.java2uml.java2umlapi.util.umlSymbols.UMLGeneratorUtil.*;

public class ParsedEnumComponent implements ParsedComponent, ParsedCompositeComponent {

    private final ResolvedEnumDeclaration resolvedEnumDeclaration;
    private final ParsedComponent parent;
    private final String name;
    private String enumConstants;
    private String methodSignatures;
    private String fieldDeclarations;
    private String constructorSignatures;
    private HashMap<String, ParsedComponent> children;

    public ParsedEnumComponent(ResolvedEnumDeclaration resolvedEnumDeclaration, ParsedComponent parent) {
        this.resolvedEnumDeclaration = resolvedEnumDeclaration;
        this.parent = parent;
        this.name = resolvedEnumDeclaration.getQualifiedName();
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
     * @return returns children of current component.
     * Empty Optional is returned if current component is a leaf.
     */
    @Override
    public Optional<Map<String, ParsedComponent>> getChildren() {
        return Optional.of(children);
    }

    /**
     * Add a child to the this component, such as field, constructor or method.
     *
     * @param parsedComponent component representing child could be a ParsedEnumConstantComponent, field, constructor,
     *                        method or any other component which can be contained in a Enum.
     */
    public void addChild(ParsedComponent parsedComponent) {
        if (children == null) {
            children = new HashMap<>();
        }

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
     * @return Returns generated UML code.
     */
    @Override
    public String toUML() {
        if (enumConstants == null) {
            enumConstants = generateUMLEnumConstantDecl(children);
        }

        if (fieldDeclarations == null) {
            fieldDeclarations = generateUMLFieldDeclarations(children);
        }

        if (constructorSignatures == null) {
            constructorSignatures = generateUMLConstructorSignatures(children);
        }

        if (methodSignatures == null) {
            methodSignatures = generateUMLMethodSignatures(children);
        }

        return "enum " + name + " {\n"
                + "..Enum Constants..\n"
                + enumConstants
                + "..Fields..\n"
                + fieldDeclarations
                + "..Methods..\n"
                + constructorSignatures
                + methodSignatures
                + "}";
    }

    @Override
    public String toString() {
        return "ParsedEnumComponent{" +
                ", name='" + name + '\'' +
                '}';
    }
}
