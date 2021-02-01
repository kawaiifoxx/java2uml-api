package org.java2uml.java2umlapi.util.uml;

/**
 * <p>
 * Provides necessary relations for generating plant uml cod,e using these relations as building blocks
 * we can generate plant uml code.
 * </p>
 *
 * @author kawaiifox
 */
public enum RelationsSymbol {
    EXTENSION("<|--"),
    COMPOSITION("*--"),
    AGGREGATION("o--"),
    ASSOCIATION("--"),
    DEPENDENCY(".."),
    ASSOCIATION_RT("-->"),
    DEPENDENCY_RT("..>");

    private final String printable;

    RelationsSymbol(String printable) {
        this.printable = printable;
    }


    @Override
    public String toString() {
        return printable;
    }
}
