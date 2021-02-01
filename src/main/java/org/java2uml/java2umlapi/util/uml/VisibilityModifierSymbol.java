package org.java2uml.java2umlapi.util.uml;

/**
 * Provides necessary visibility modifiers for generating plant uml code, using these visibility modifiers as building blocks
 * we can generate plant uml code.
 *
 * @author kawaiifox
 */
public enum VisibilityModifierSymbol {
    PRIVATE("-"),
    PROTECTED("#"),
    PUBLIC("+");

    private final String printable;

    VisibilityModifierSymbol(String printable) {
        this.printable = printable;
    }


    @Override
    public String toString() {
        return printable;
    }
}
