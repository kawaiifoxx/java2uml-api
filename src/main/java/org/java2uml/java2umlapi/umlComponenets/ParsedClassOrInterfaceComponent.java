package org.java2uml.java2umlapi.umlComponenets;

import com.github.javaparser.resolution.declarations.ResolvedDeclaration;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.java2uml.java2umlapi.util.umlSymbols.TypeDeclarationSymbol.getTypeDeclarationSymbol;

/**
 * <p>
 * A composite Component, containing other classes or interfaces, or methods or fields as children.
 * </p>
 *
 * @author kawaiifox
 */
public class ParsedClassOrInterfaceComponent implements ParsedComponent {

    private final ResolvedDeclaration resolvedDeclaration;

    private final ParsedComponent parent;

    private final String name;

    private String methodSignatures;

    private String constructorSignatures;

    private String fieldsDeclarations;

    private String typeDeclaration;

    private Map<String, ParsedComponent> children;

    /**
     * Initializes ParsedClassOrInterfaceComponent with resolvedDeclaration and reference to parent.
     * @param resolvedDeclaration ResolvedDeclaration received after typeSolving using a symbol resolver.
     * @param parent parent of this component.
     */
    public ParsedClassOrInterfaceComponent(ResolvedDeclaration resolvedDeclaration, ParsedComponent parent) {
        this.resolvedDeclaration = resolvedDeclaration;
        this.parent = parent;
        this.name = resolvedDeclaration.asType().getQualifiedName();
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
    public Optional<Map<String, ParsedComponent>> getChildren() {
        if (children == null)
            return Optional.empty();
        return Optional.of(children);
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Add a child to the this component, such as field, constructor or method.
     * @param parsedComponent component representing child could be field, constructor,
     *                        method or any other component which can be contained in a Class or Interface.
     */
    public void addChild(ParsedComponent parsedComponent) {
        if (children == null) {
            children = new HashMap<>();
        }

        children.put(parsedComponent.getName(), parsedComponent);
    }

    /**
     * Generates a single String containing all the method signatures,separated by newline character.
     * for each child which is ParsedMethodComponent get there UML generated string and append them
     * together with newline character in between.
     */
    private void generateUMLMethodSignatures() {
        StringBuilder generatedSignatures = new StringBuilder();

        children.forEach((k, v) -> {
            if (v.isParsedMethodComponent()) {
                generatedSignatures.append(v.toUML()).append("\n");
            }
        });

        methodSignatures = generatedSignatures.toString();
    }

    /**
     * Generates a single String containing all the constructor signatures,separated by newline character.
     * for each child which is ParsedConstructorComponent get there UML generated string and append them
     * together with newline character in between.
     */
    private void generateUMLConstructorSignatures() {
        StringBuilder generatedConstructorSignatures = new StringBuilder();

        children.forEach((k, v) -> {
            if (v.isParsedConstructorComponent()) {
                generatedConstructorSignatures.append(v.toUML()).append("\n");
            }
        });

        constructorSignatures = generatedConstructorSignatures.toString();
    }

    /**
     * Generates a single String containing all the field declarations, separated by newline character.
     * for each child which is ParsedFieldComponent get there UML generated string and append them
     * together with newline character in between.
     */
    private void generateUMLFieldDeclarations() {
        StringBuilder generatedFields = new StringBuilder();

        children.forEach((k, v) -> {
            if (v.isParseFieldComponent()) {
                generatedFields.append(v.toUML()).append("\n");
            }
        });

        fieldsDeclarations = generatedFields.toString();
    }

    /**
     * Generate uml for this ParsedClassOrInterFaceComponent.
     * @return String containing generated uml for this class.
     */
    @Override
    public String toUML() {
        if (fieldsDeclarations == null) {
            generateUMLFieldDeclarations();
        }

        if (methodSignatures == null) {
            generateUMLMethodSignatures();
        }

        if (constructorSignatures == null) {
            generateUMLConstructorSignatures();
        }

        if (typeDeclaration == null) {
            typeDeclaration = getTypeDeclarationSymbol(resolvedDeclaration.asType());
        }

        return typeDeclaration + " {\n"
                + fieldsDeclarations
                + constructorSignatures
                + methodSignatures
                + "}";
    }

    @Override
    public String toString() {
        return "ParsedClassOrInterfaceComponent{" +
                "  name='" + name + '\'' +
                ", methodSignatures='" + methodSignatures + '\'' +
                ", constructorSignatures='" + constructorSignatures + '\'' +
                ", fieldsDeclarations='" + fieldsDeclarations + '\'' +
                ", typeDeclaration='" + typeDeclaration + '\'' +
                '}';
    }
}
