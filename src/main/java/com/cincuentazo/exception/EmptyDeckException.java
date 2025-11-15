package com.cincuentazo.exception;

/**
 * Custom checked exception thrown when attempting to draw from an empty deck.
 * This is a marked (checked) exception that extends GameException.
 *
 * @author Cincuentazo Team
 * @version 1.0.0
 */
public class EmptyDeckException extends GameException {

    /**
     * Constructs an EmptyDeckException with the specified message.
     *
     * @param message the detail message
     */
    public EmptyDeckException(String message) {
        super(message);
    }

    /**
     * Constructs an EmptyDeckException with message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public EmptyDeckException(String message, Throwable cause) {
        super(message, cause);
    }
}