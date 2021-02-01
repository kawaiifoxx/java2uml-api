package org.java2uml.java2umlapi.util.umlSymbols;
/**
 * <p>
 * Provides UMLModifiers, so that different fields and methods can be classified.
 * </p>
 *
 * @author kawaiifox
 */
public enum UMLModifiers {
    FIELD("{field}"),
    METHOD("{method}"),
    ABSTRACT("{abstract}"),
    STATIC("{static}");

    private final String printable;


    UMLModifiers(String printable) {
        this.printable = printable;
    }

    @Override
    public String toString() {
        return printable;
    }
}
