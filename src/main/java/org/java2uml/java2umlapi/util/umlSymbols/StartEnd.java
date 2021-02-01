package org.java2uml.java2umlapi.util.umlSymbols;

/**
 * <p>
 *     Defines start end tags for plant uml code.
 * </p>
 *
 * @author kawaiifox
 */
public enum StartEnd {
    START("@startuml"),
    END("@enduml");

    private final String printable;

    StartEnd(String printable) {
        this.printable = printable;
    }

    @Override
    public String toString() {
        return printable;
    }
}
