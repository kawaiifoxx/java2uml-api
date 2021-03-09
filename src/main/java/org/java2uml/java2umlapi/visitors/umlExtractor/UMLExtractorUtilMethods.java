package org.java2uml.java2umlapi.visitors.umlExtractor;

import com.github.javaparser.resolution.declarations.ResolvedDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import org.java2uml.java2umlapi.umlComponenets.ParsedClassOrInterfaceComponent;
import org.java2uml.java2umlapi.umlComponenets.ParsedComponent;
import org.java2uml.java2umlapi.umlComponenets.TypeRelation;

import java.util.Map;
import java.util.Set;

public abstract class UMLExtractorUtilMethods {
    /**
     * Generates a single String containing all the Enum constants, separated by newline character.
     * for each child which is ParsedEnumConstantComponent get there UML generated string and append them
     * together with newline character in between.
     */
    static String generateUMLEnumConstantDecl(Map<String, ParsedComponent> children,
                                              UMLExtractor umlExtractor) {
        StringBuilder generatedEnumConst = new StringBuilder();

        children.forEach((k, v) -> {
            if (v.isParsedEnumConstantComponent()) {
                generatedEnumConst.append(v.accept(umlExtractor)).append("\n");
            }
        });

        return generatedEnumConst.toString();
    }

    /**
     * Generates a single String containing all the constructor signatures,separated by newline character.
     * for each child which is ParsedConstructorComponent get there UML generated string and append them
     * together with newline character in between.
     */
    static String generateUMLConstructorSignatures(Map<String, ParsedComponent> children,
                                                   UMLExtractor umlExtractor) {
        StringBuilder generatedConstructorSignatures = new StringBuilder();

        children.forEach((k, v) -> {
            if (v.isParsedConstructorComponent()) {
                generatedConstructorSignatures.append(v.accept(umlExtractor)).append("\n");
            }
        });

        return generatedConstructorSignatures.toString();
    }

    /**
     * Generates a single String containing all the field declarations, separated by newline character.
     * for each child which is ParsedFieldComponent get there UML generated string and append them
     * together with newline character in between.
     */
    static String generateUMLFieldDeclarations(Map<String, ParsedComponent> children, UMLExtractor umlExtractor) {
        StringBuilder generatedFields = new StringBuilder();

        children.forEach((k, v) -> {
            if (v.isParsedFieldComponent()) {
                generatedFields.append(v.accept(umlExtractor)).append("\n");
            }
        });

        return generatedFields.toString();
    }

    /**
     * Generates a single String containing all the method signatures,separated by newline character.
     * for each child which is ParsedMethodComponent get there UML generated string and append them
     * together with newline character in between.
     */
    static String generateUMLMethodSignatures(Map<String, ParsedComponent> children, UMLExtractor umlExtractor) {
        StringBuilder generatedSignatures = new StringBuilder();

        children.forEach((k, v) -> {
            if (v.isParsedMethodComponent()) {
                generatedSignatures.append(v.accept(umlExtractor)).append("\n");
            }
        });

        return generatedSignatures.toString();
    }

    /**
     * For each parsedComponent in children and externalComponent, this method injects
     * UMLExtractor in it and appends the returned UML to a string with a newline
     * character in between.
     */
    static String generateUMLClasses(Map<String, ParsedComponent> children,
                                     Map<String, ParsedComponent> externalComponents,
                                     UMLExtractor umlExtractor) {
        StringBuilder generatedUMLClassesBuilder = new StringBuilder();

        children.forEach((k, v) ->
                generatedUMLClassesBuilder.append(v.accept(umlExtractor)).append("\n"));
        externalComponents.forEach((k, v) ->
                generatedUMLClassesBuilder.append(v.accept(umlExtractor)).append("\n"));

        return generatedUMLClassesBuilder.toString();
    }

    /**
     * For each typeRelation in allRelations use .toUML() on it and appends a newline character, this generates
     * a single string containing UML of all the relations.
     */
    static String generateUMLTypeRelations(Set<TypeRelation> allRelations, UMLExtractor umlExtractor) {
        StringBuilder generatedUMLTypesRelationsBuilder = new StringBuilder();

        allRelations.forEach(e ->
                generatedUMLTypesRelationsBuilder.append(e.accept(umlExtractor)).append("\n"));

        return generatedUMLTypesRelationsBuilder.toString();
    }

    /**
     * Gets the correct return type from resolvedDeclaration and then returns it in string form.
     *
     * @return returns string of return type.
     */
    static String getReturnType(ResolvedMethodDeclaration resolvedDeclaration) {
        var resolvedType = resolvedDeclaration.getReturnType();

        if (resolvedType.isVoid()) {
            return "void";
        }

        if (resolvedType.isReferenceType()) {
            var qualifiedReturnType = resolvedType.asReferenceType().getQualifiedName().split("\\.");
            return qualifiedReturnType[qualifiedReturnType.length - 1];
        }

        return resolvedType.asPrimitive().name().toLowerCase();
    }

    static ResolvedDeclaration getResolvedDeclaration(ParsedClassOrInterfaceComponent parsedClassOrInterfaceComponent) {
        return parsedClassOrInterfaceComponent.getResolvedDeclaration()
                .orElseThrow(() -> new IllegalStateException("ParsedComponent should contain resolvedDeclaration without it uml cannot be generated."));
    }


    static String getClassOfField(ResolvedFieldDeclaration resolvedDeclaration) {
        if (resolvedDeclaration.getType().isReferenceType()) {
            var list = resolvedDeclaration.getType().asReferenceType().getQualifiedName().split("\\.");
            return list[list.length - 1];
        }

        return resolvedDeclaration.getType().asPrimitive().name()
                + (resolvedDeclaration.getType().asPrimitive().isArray() ? "[]" : "");
    }

}
