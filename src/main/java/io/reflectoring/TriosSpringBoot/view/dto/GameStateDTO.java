package io.reflectoring.TriosSpringBoot.view.dto;

import io.reflectoring.TriosSpringBoot.model.PlayerColor;
import java.util.List;

/**
 * DTO representing the complete state of a game.
 */
public record GameStateDTO(
        String gameId,
        String gameState,
        PlayerColor currentPlayer,
        BoardState board,
        List<CardDTO> redPlayerHand,
        List<CardDTO> bluePlayerHand,
        Integer redScore,
        Integer blueScore,
        String winner,  // null if game not over
        String selectedCard  // ID of currently selected card, null if none
) {
    /**
     * Creates a new GameStateDTO with validation.
     */
    public GameStateDTO {
        if (gameId == null || gameId.isBlank()) {
            throw new IllegalArgumentException("Game ID cannot be null or blank");
        }
        if (gameState == null || gameState.isBlank()) {
            throw new IllegalArgumentException("Game state cannot be null or blank");
        }
        if (currentPlayer == null) {
            throw new IllegalArgumentException("Current player cannot be null");
        }
        if (board == null) {
            throw new IllegalArgumentException("Board cannot be null");
        }
        if (redPlayerHand == null || bluePlayerHand == null) {
            throw new IllegalArgumentException("Player hands cannot be null");
        }
        if (redScore == null || blueScore == null) {
            throw new IllegalArgumentException("Scores cannot be null");
        }
    }

    /**
     * Gets the hand of the specified player.
     */
    public List<CardDTO> getPlayerHand(PlayerColor player) {
        return switch (player) {
            case RED -> redPlayerHand;
            case BLUE -> bluePlayerHand;
        };
    }

    /**
     * Gets the score of the specified player.
     */
    public int getPlayerScore(PlayerColor player) {
        return switch (player) {
            case RED -> redScore;
            case BLUE -> blueScore;
        };
    }

    /**
     * Checks if the game is over.
     */
    public boolean isGameOver() {
        return winner != null;
    }
}