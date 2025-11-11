package com.cincuentazo.exception;

/**
 * Custom unchecked exception for invalid card operations.
 * This is an unmarked (unchecked) exception that extends RuntimeException.
 * Used when a card operation violates game rules or logic.
 *
 * @author Cincuentazo Team
 * @version 1.0.0
 */
public class InvalidCardException extends RuntimeException {

    /**
     * Constructs an InvalidCardException with the specified message.
     *
     * @param message the detail message
     */
    public InvalidCardException(String message) {
        super(message);
    }

    /**
     * Constructs an InvalidCardException with message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public InvalidCardException(String message, Throwable cause) {
        super(message, cause);
    }
}