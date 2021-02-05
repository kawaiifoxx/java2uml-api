package org.java2uml.java2umlapi.util.umlSymbols;

import com.github.javaparser.resolution.declarations.ResolvedTypeDeclaration;
import org.jetbrains.annotations.NotNull;

/**
 * <p>
 * Provides necessary TypeDeclarationSymbols for plant uml syntax generation,
 * such as class, interface, enum, annotation. we can parametrize these types too.
 * </p>
 *
 * @author kawaiifox
 */
public class TypeDeclarationSymbol {
    /**
     * Sets printable to provided parameter.
     * <p>
     * After passing this method toString should return,
     * for e.g. if toString is called on CLASS
     * then it should return "class <{typeParamList}>"
     *
     * @param typeParamList   name of the parameter for generic.
     * @param typeDeclaration name of type declaration like class, interface, annotation, enum.
     */
    private static String parametrizeOn(String typeParamList, String typeDeclaration, String typeName) {
        return typeDeclaration + " " + typeName + " <" + typeParamList + ">";
    }

    /**
     * Generates type declaration symbols from resolvedTypeDeclaration, declaration can be generated for generic classes as well
     * which have type parameters.
     * @param resolvedDeclaration Resolved type declaration of classOrInterface for which symbol needs to be generated.
     * @return Symbol containing keywords like class, interface with typeDeclaration.
     */
    public static String getTypeDeclarationSymbol(ResolvedTypeDeclaration resolvedDeclaration) {
        var typeDeclaration = "class " + resolvedDeclaration.getQualifiedName();

        if (resolvedDeclaration.isClass()) {
            typeDeclaration = "class " + resolvedDeclaration.getQualifiedName();
            if (resolvedDeclaration.asReferenceType().isGeneric()) {
                typeDeclaration = "class";
                typeDeclaration = parametrizeOn(getTypeParamsString(resolvedDeclaration).toString().trim()
                        , typeDeclaration, resolvedDeclaration.getQualifiedName());
            }
        } else if (resolvedDeclaration.isInterface()) {
            typeDeclaration = "interface " + resolvedDeclaration.getQualifiedName();
            if (resolvedDeclaration.asReferenceType().isGeneric()) {
                typeDeclaration = "interface";
                typeDeclaration = parametrizeOn(getTypeParamsString(resolvedDeclaration).toString().trim()
                        , typeDeclaration, resolvedDeclaration.getQualifiedName());
            }
        }

        return typeDeclaration;
    }

    /**
     * Generates string containing all the type parameters separated by ", ".
     * @param resolvedDeclaration Resolved type declaration of classOrInterface for which symbol needs to be generated.
     * @return a String containing all the type parameters separated by ", ".
     */
    @NotNull
    private static StringBuilder getTypeParamsString(ResolvedTypeDeclaration resolvedDeclaration) {
        var typeParams = resolvedDeclaration
                .asReferenceType()
                .getTypeParameters();

        StringBuilder typeParamsString = new StringBuilder();

        typeParams.forEach(typeParam ->
                typeParamsString.append(typeParam.getName()).append(", "));
        typeParamsString.deleteCharAt(typeParamsString.length() - 2);
        return typeParamsString;
    }
}
