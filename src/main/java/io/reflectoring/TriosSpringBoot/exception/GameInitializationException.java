package io.reflectoring.TriosSpringBoot.exception;

/**
 * Exception thrown when game initialization fails.
 */
public class GameInitializationException extends RuntimeException {
    public GameInitializationException(String message) {
        super(message);
    }
}
