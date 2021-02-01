package org.java2uml.java2umlapi.util.umlSymbols;

public enum Separators {

    DOTTED(".."),
    LINE("--"),
    THICK_LINE("__"),
    DOUBLE_LINE("==");

    private String printable;

    Separators(String printable) {
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
