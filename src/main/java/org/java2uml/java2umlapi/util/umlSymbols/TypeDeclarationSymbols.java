package org.java2uml.java2umlapi.util.umlSymbols;

public enum TypeDeclarationSymbols {

    CLASS("class"),
    ABSTRACT("abstract"),
    INTERFACE("interface"),
    ENUM("enum"),
    ANNOTATION("annotation");

    private String printable;

    TypeDeclarationSymbols(String printable) {
        this.printable = printable;
    }

    public void parametrizeOn(String printable) {
        this.printable = this.printable + " <" + printable + ">";
    }

    @Override
    public String toString() {
        return printable;
    }
}
