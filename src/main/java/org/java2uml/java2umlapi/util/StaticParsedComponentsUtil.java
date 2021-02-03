package org.java2uml.java2umlapi.util;

import com.github.javaparser.ast.AccessSpecifier;
import com.github.javaparser.resolution.declarations.ResolvedDeclaration;
import org.java2uml.java2umlapi.util.umlSymbols.VisibilityModifierSymbol;
import org.jetbrains.annotations.NotNull;


/**
 * <p>
 * Collection of utility methods required in classes in package umlComponents.
 * </p>
 *
 * @author kawaiifox
 */
public class StaticParsedComponentsUtil {
    @NotNull
    public static VisibilityModifierSymbol getVisibilityModifierSymbol(ResolvedDeclaration resolvedDeclaration) {
        AccessSpecifier accessSpecifier = null;

        if (resolvedDeclaration.isField()) {
            accessSpecifier = resolvedDeclaration.asField().accessSpecifier();
        } else if (resolvedDeclaration.isMethod()) {
            accessSpecifier = resolvedDeclaration.asMethod().accessSpecifier();
        }

        if (accessSpecifier != null) {
            VisibilityModifierSymbol visibilityModifier;

            switch (accessSpecifier.toString()) {
                case "public":
                    visibilityModifier = VisibilityModifierSymbol.PUBLIC;
                    break;
                case "private":
                    visibilityModifier = VisibilityModifierSymbol.PRIVATE;
                    break;
                case "protected":
                    visibilityModifier = VisibilityModifierSymbol.PROTECTED;
                    break;
                default:
                    visibilityModifier = VisibilityModifierSymbol.PKG_PRIVATE;
            }
            return visibilityModifier;
        }

        return VisibilityModifierSymbol.PKG_PRIVATE;
    }

    public static String getClassOfField(ResolvedDeclaration resolvedDeclaration) {
        if (!resolvedDeclaration.isField()) {
            throw new RuntimeException("Provided resolvedDeclaration is not a fieldDeclaration, unable to get class of field.");
        }

        return resolvedDeclaration.asField().declaringType().getClassName();
    }
}
