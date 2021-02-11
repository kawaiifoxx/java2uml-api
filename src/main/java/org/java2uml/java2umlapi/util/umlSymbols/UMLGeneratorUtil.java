package org.java2uml.java2umlapi.util.umlSymbols;

import org.java2uml.java2umlapi.umlComponenets.ParsedComponent;

import java.util.Map;

public abstract class UMLGeneratorUtil {

    /**
     * Generates a single String containing all the Enum constants, separated by newline character.
     * for each child which is ParsedEnumConstantComponent get there UML generated string and append them
     * together with newline character in between.
     */
    public static String generateUMLEnumConstantDecl(Map<String, ParsedComponent> children) {
        StringBuilder generatedEnumConst = new StringBuilder();

        children.forEach((k, v) -> {
            if (v.isParsedEnumConstantComponent()) {
                generatedEnumConst.append(v.toUML()).append("\n");
            }
        });

        return generatedEnumConst.toString();
    }

    /**
     * Generates a single String containing all the constructor signatures,separated by newline character.
     * for each child which is ParsedConstructorComponent get there UML generated string and append them
     * together with newline character in between.
     */
    public static String generateUMLConstructorSignatures(Map<String, ParsedComponent> children) {
        StringBuilder generatedConstructorSignatures = new StringBuilder();

        children.forEach((k, v) -> {
            if (v.isParsedConstructorComponent()) {
                generatedConstructorSignatures.append(v.toUML()).append("\n");
            }
        });

        return generatedConstructorSignatures.toString();
    }

    /**
     * Generates a single String containing all the field declarations, separated by newline character.
     * for each child which is ParsedFieldComponent get there UML generated string and append them
     * together with newline character in between.
     */
    public static String generateUMLFieldDeclarations(Map<String, ParsedComponent> children) {
        StringBuilder generatedFields = new StringBuilder();

        children.forEach((k, v) -> {
            if (v.isParsedFieldComponent()) {
                generatedFields.append(v.toUML()).append("\n");
            }
        });

        return generatedFields.toString();
    }

    /**
     * Generates a single String containing all the method signatures,separated by newline character.
     * for each child which is ParsedMethodComponent get there UML generated string and append them
     * together with newline character in between.
     */
    public static String generateUMLMethodSignatures(Map<String, ParsedComponent> children) {
        StringBuilder generatedSignatures = new StringBuilder();

        children.forEach((k, v) -> {
            if (v.isParsedMethodComponent()) {
                generatedSignatures.append(v.toUML()).append("\n");
            }
        });

        return generatedSignatures.toString();
    }

}
