package io.reflectoring.TriosSpringBoot.exception;

/**
 * Exception thrown when a game cannot be found.
 */
public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(String message) {
        super(message);
    }
}

