package com.cincuentazo.exception;

/**
 * Custom checked exception for general game-related errors.
 * This is a marked (checked) exception that must be caught or declared.
 *
 * @author William May, Miguel Martinez
 * @version 1.0.0
 */
public class GameException extends Exception {

    /**
     * Constructs a GameException with the specified message.
     *
     * @param message the detail message
     */
    public GameException(String message) {
        super(message);
    }

    /**
     * Constructs a GameException with message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public GameException(String message, Throwable cause) {
        super(message, cause);
    }
}
