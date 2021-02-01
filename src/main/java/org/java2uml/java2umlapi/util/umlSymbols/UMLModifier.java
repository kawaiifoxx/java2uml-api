package org.java2uml.java2umlapi.util.umlSymbols;
/**
 * <p>
 * Provides UMLModifiers, so that different fields and methods can be classified.
 * </p>
 *
 * @author kawaiifox
 */
public enum UMLModifier {
    FIELD("{field}"),
    METHOD("{method}"),
    ABSTRACT("{abstract}"),
    STATIC("{static}");

    private final String printable;


    UMLModifier(String printable) {
        this.printable = printable;
    }

    @Override
    public String toString() {
        return printable;
    }
}
