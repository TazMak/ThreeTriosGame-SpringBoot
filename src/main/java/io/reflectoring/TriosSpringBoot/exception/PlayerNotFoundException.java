package io.reflectoring.TriosSpringBoot.exception;

/**
 * Exception thrown when a player cannot be found.
 */
public class PlayerNotFoundException extends RuntimeException {
    public PlayerNotFoundException(String message) {
        super(message);
    }
}
