package org.java2uml.java2umlapi.util.umlSymbols;
/**
 * <p>
 * Provides necessary separators for generating plant uml code using these separators as building blocks
 * we can generate plant uml code.
 * </p>
 *
 * @author kawaiifox
 */
public enum Separator {

    DOTTED(".."),
    LINE("--"),
    THICK_LINE("__"),
    DOUBLE_LINE("==");

    private String printable;

    Separator(String printable) {
        this.printable = printable;
    }

    public void setPrintable(String printable) {
        this.printable = this.printable + printable + this.printable;
    }

    @Override
    public String toString() {
        return printable;
    }
}
