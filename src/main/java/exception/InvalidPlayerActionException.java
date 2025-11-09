package exception;

/**
 * Custom unchecked exception for invalid player actions.
 * This is an unmarked (unchecked) exception.
 *
 * @author William May, Miguel Martinez
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
}