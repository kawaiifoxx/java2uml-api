package org.java2uml.java2umlapi.util.umlSymbols;

/**
 * <p>
 * Provides necessary visibility modifiers for generating plant uml code, using these visibility modifiers as building blocks
 * we can generate plant uml code.
 * </p>
 *
 * @author kawaiifox
 */
public enum VisibilityModifierSymbol {
    PRIVATE("-"),
    PROTECTED("#"),
    PUBLIC("+"),
    PKG_PRIVATE("~");

    private final String printable;

    VisibilityModifierSymbol(String printable) {
        this.printable = printable;
    }


    @Override
    public String toString() {
        return printable;
    }

    public static VisibilityModifierSymbol of(String accessModifier) {
        switch (accessModifier) {
            case "public" :
                return PUBLIC;
            case "private":
                return PRIVATE;
            case "protected":
                return PROTECTED;
            default:
                return PKG_PRIVATE;
        }
    }
}
