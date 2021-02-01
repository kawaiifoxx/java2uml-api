package org.java2uml.java2umlapi.util.umlSymbols;

/**
 * <p>
 * Provides necessary TypeDeclarationSymbols for plant uml syntax generation,
 * such as class, interface, enum, annotation. we can parametrize these types too.
 * </p>
 *
 * @author kawaiifox
 */
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

    /**
     * Sets printable to provided parameter.
     * <p>
     * After passing this method toString should return,
     * for e.g. if toString is called on CLASS
     * then it should return "class <{printable}>"
     *
     * @param printable name of the parameter for generic.
     */
    public void parametrizeOn(String printable) {
        this.printable = this.printable + " <" + printable + ">";
    }

    @Override
    public String toString() {
        return printable;
    }
}
