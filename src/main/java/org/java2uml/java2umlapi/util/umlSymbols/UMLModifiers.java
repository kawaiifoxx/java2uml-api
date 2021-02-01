package org.java2uml.java2umlapi.util.umlSymbols;

public enum UMLModifiers {
    FIELD("{field}"),
    METHOD("{method}"),
    ABSTRACT("{abstract}"),
    STATIC("{static}");

    private final String printable;


    UMLModifiers(String printable) {
        this.printable = printable;
    }
}
