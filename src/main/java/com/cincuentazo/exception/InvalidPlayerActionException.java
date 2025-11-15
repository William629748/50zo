package com.cincuentazo.exception;

/**
 * Custom unchecked exception for invalid player actions.
 * This is an unmarked (unchecked) exception that extends RuntimeException.
 * Used when a player attempts an action that is not allowed.
 *
 * @author Cincuentazo Team
 * @version 1.0.0
 */
public class InvalidPlayerActionException extends RuntimeException {

    /**
     * Constructs an InvalidPlayerActionException with the specified message.
     *
     * @param message the detail message
     */
    public InvalidPlayerActionException(String message) {
        super(message);
    }

    /**
     * Constructs an InvalidPlayerActionException with message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public InvalidPlayerActionException(String message, Throwable cause) {
        super(message, cause);
    }
}