package com.cincuentazo.exception;

/**
 * Custom checked exception thrown when attempting to draw from an empty deck.
 * This is a marked (checked) exception.
 *
 * @author William May, Miguel Martinez
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
}