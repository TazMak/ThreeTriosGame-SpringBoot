package io.reflectoring.TriosSpringBoot.model;

import io.reflectoring.TriosSpringBoot.strategy.Strategy;
import io.reflectoring.TriosSpringBoot.exception.InvalidMoveException;

/**
 * Interface for AI-capable players.
 * Defines the contract for AI player behavior.
 */
public interface AICapable {
    /**
     * Makes an automatic move using the AI strategy.
     *
     * @return true if move was successful, false otherwise
     * @throws InvalidMoveException if the move is invalid
     * @throws IllegalStateException if game is in invalid state for move
     */
    boolean makeMove();

    /**
     * Gets the strategy being used by this AI player.
     *
     * @return the strategy instance
     */
    Strategy getStrategy();

    /**
     * Checks if it's this AI's turn to move.
     *
     * @return true if it's this AI's turn, false otherwise
     */
    default boolean isMyTurn() {
        return false; // Implementation should be provided by concrete classes
    }
}