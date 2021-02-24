package org.java2uml.java2umlapi.umlComponenets;

import com.github.javaparser.resolution.declarations.ResolvedDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedEnumDeclaration;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.java2uml.java2umlapi.util.umlSymbols.Separator.DOTTED;
import static org.java2uml.java2umlapi.util.umlSymbols.UMLGeneratorUtil.*;

public class ParsedEnumComponent implements ParsedCompositeComponent {

    private final ResolvedEnumDeclaration resolvedEnumDeclaration;
    private final ParsedComponent parent;
    private final String name;
    private String enumConstants;
    private String methodSignatures;
    private String fieldDeclarations;
    private String constructorSignatures;
    private final HashMap<String, ParsedComponent> children;

    public ParsedEnumComponent(ResolvedEnumDeclaration resolvedEnumDeclaration, ParsedComponent parent) {
        this.resolvedEnumDeclaration = resolvedEnumDeclaration;
        this.parent = parent;
        this.name = resolvedEnumDeclaration.getQualifiedName();
        this.children = new HashMap<>();
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
                + DOTTED + "Enum Constants" + DOTTED + "\n"
                + enumConstants
                + DOTTED + "Fields" + DOTTED + "\n"
                + fieldDeclarations
                + DOTTED + "Methods" + DOTTED + "\n"
                + constructorSignatures
                + methodSignatures
                + "}";
    }

    @Override
    public String toString() {
        return "ParsedEnumComponent{" +
                "name='" + name + '\'' +
                '}';
    }
}
