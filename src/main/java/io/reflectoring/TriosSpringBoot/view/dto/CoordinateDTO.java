package io.reflectoring.TriosSpringBoot.view.dto;

/**
 * DTO for board coordinates.
 */
public record CoordinateDTO(
        int row,
        int col
) {
    /**
     * Creates a new CoordinateDTO with validation.
     */
    public CoordinateDTO {
        if (row < 0) {
            throw new IllegalArgumentException("Row cannot be negative");
        }
        if (col < 0) {
            throw new IllegalArgumentException("Column cannot be negative");
        }
    }
}