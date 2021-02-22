package org.java2uml.java2umlapi.exceptions;

/**
 * <p>
 * This exception is thrown when a ParsedComponent is not source component and its parent is not found.
 * </p>
 *
 * @author kawaiifox
 */
public class OrphanComponentException extends IllegalStateException {
    /**
     * Constructs an IllegalStateException with the specified detail
     * message.  A detail message is a String that describes this particular
     * exception.
     *
     * @param s the String that contains a detailed message
     */
    public OrphanComponentException(String s) {
        super(s);
    }
}
