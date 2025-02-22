package io.reflectoring.TriosSpringBoot.view.dto;

import io.reflectoring.TriosSpringBoot.entity.Game;
import java.time.LocalDateTime;

/**
 * DTO for game creation and status responses.
 */
public record GameResponseDTO(
        Long id,
        String state,
        String currentPlayer,
        LocalDateTime createdAt,
        boolean isActive
) {
    /**
     * Creates a new GameResponseDTO from a Game entity.
     */
    public GameResponseDTO(Game game) {
        this(
                game.getId(),
                game.getGameState(),
                game.getCurrentPlayer(),
                game.getCreatedAt(),
                game.isActive()
        );
    }

    /**
     * Creates a new GameResponseDTO with validation.
     */
    public GameResponseDTO {
        if (id == null) {
            throw new IllegalArgumentException("Game ID cannot be null");
        }
        if (state == null || state.isBlank()) {
            throw new IllegalArgumentException("State cannot be null or blank");
        }
        if (currentPlayer == null || currentPlayer.isBlank()) {
            throw new IllegalArgumentException("Current player cannot be null or blank");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("Created at cannot be null");
        }
    }
}