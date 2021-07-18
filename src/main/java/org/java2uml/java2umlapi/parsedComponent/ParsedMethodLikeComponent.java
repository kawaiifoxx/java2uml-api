package org.java2uml.java2umlapi.parsedComponent;

import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedMethodLikeDeclaration;

/**
 * This interface provides some useful functionality to method like components.
 *
 * @author kawaiifoxx
 */
public interface ParsedMethodLikeComponent extends ParsedComponent {
    /**
     * @return The qualified signature of the method. It is composed by the qualified name of the declaring type
     * followed by the signature of the method.
     */
    default <T extends ResolvedMethodLikeDeclaration> String getQualifiedSignature(T resolvedDeclaration) {
        String signature;
        try {
            signature = resolvedDeclaration.getQualifiedSignature();
        } catch (UnsolvedSymbolException e) {
            return resolvedDeclaration.declaringType().getId() + "." + getSignature(resolvedDeclaration);
        }

        return signature;
    }

    /**
     * @return Signature of the method
     */
    default <T extends ResolvedMethodLikeDeclaration> String getSignature(T resolvedDeclaration) {
        try {
            return resolvedDeclaration.getSignature();
        } catch (UnsolvedSymbolException e) {
            var sigBuilder = new StringBuilder();
            sigBuilder.append(resolvedDeclaration.getName())
                    .append('(');

            for (int i = 0; i < resolvedDeclaration.getNumberOfParams(); i++) {
                if (i != 0) {
                    sigBuilder.append(", ");
                }
                sigBuilder.append(getParamTypeName(resolvedDeclaration, i));
            }
            return sigBuilder.append(')').toString();
        }
    }

    /**
     * @param resolvedDeclaration from which type parameter will be retrieved.
     * @param i index of method parameter you want to retrieve.
     * @return Type name of the parameter at index i.
     */
    private <T extends ResolvedMethodLikeDeclaration> String getParamTypeName(T resolvedDeclaration, int i) {
        String typeName;
        try {
            typeName = resolvedDeclaration.getParam(i).getType().describe();
        } catch (UnsolvedSymbolException exception) {
            return exception.getName();
        }

        return typeName;
    }
}
