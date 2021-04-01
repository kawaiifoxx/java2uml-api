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
    EXTENSION("--|>", "EXTENSION"),
    COMPOSITION("*--", "COMPOSITION"),
    AGGREGATION("o--", "AGGREGATION"),
    ASSOCIATION("--", "ASSOCIATION"),
    DEPENDENCY("..", "DEPENDENCY"),
    ASSOCIATION_AR("->", "ASSOCIATION"),
    DEPENDENCY_AR(".>", "DEPENDENCY");

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

    private final String name;

    RelationsSymbol(String printable, String name) {
        this.printable = printable;
        this.name = name;
    }

    public String getName() {
        return name;
    }


    @Override
    public String toString() {
        return printable;
    }
}
