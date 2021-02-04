package org.java2uml.java2umlapi.util.umlSymbols;

/**
 * <p>
 * Provides necessary relations for generating plant uml cod,e using these relations as building blocks
 * we can generate plant uml code.
 * </p>
 *
 * @author kawaiifox
 */
public enum RelationsSymbol {
    EXTENSION("--|>"),
    COMPOSITION("*--"),
    AGGREGATION("o--"),
    ASSOCIATION("--"),
    DEPENDENCY(".."),
    ASSOCIATION_AR("->"),
    DEPENDENCY_AR(".>");

    public enum Direction {
        UP("-up"),
        DW("-down"),
        RT("-right"),
        LT("-left");

        private final String printable;

        Direction(String printable) {
            this.printable = printable;
        }

        @Override
        public String toString() {
            return printable;
        }
    }

    private final String printable;

    RelationsSymbol(String printable) {
        this.printable = printable;
    }


    @Override
    public String toString() {
        return printable;
    }
}
