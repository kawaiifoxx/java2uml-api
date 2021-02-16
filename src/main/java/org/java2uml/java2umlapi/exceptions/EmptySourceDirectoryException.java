package org.java2uml.java2umlapi.exceptions;

/**
 * <p>
 *     This exception can be thrown when parser encounters that source directory being parsed is empty.
 * </p>
 *
 * @author kawaiifox
 */
public class EmptySourceDirectoryException extends RuntimeException {
    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public EmptySourceDirectoryException(String message) {
        super(message);
    }
}
