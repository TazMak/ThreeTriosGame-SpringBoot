package io.reflectoring.TriosSpringBoot.view.dto;

import java.util.Map;

/**
 * DTO representing a card in the game.
 */
public record CardDTO(
        String id,
        String owner,
        Map<String, Integer> values  // Direction -> Value mapping
) {
    /**
     * Creates a new CardDTO with validation.
     */
    public CardDTO {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Card ID cannot be null or blank");
        }
        if (owner == null || owner.isBlank()) {
            throw new IllegalArgumentException("Owner cannot be null or blank");
        }
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("Values cannot be null or empty");
        }
        // Validate that all required directions are present
        var requiredDirections = java.util.Set.of("NORTH", "SOUTH", "EAST", "WEST");
        if (!values.keySet().containsAll(requiredDirections)) {
            throw new IllegalArgumentException("Missing required directions");
        }
    }
}