package io.reflectoring.TriosSpringBoot.view.dto;

/**
 * DTO representing a single cell on the game board.
 */
public record CellDTO(
        String type,  // "CARD", "EMPTY", or "HOLE"
        CardDTO card, // null if no card present
        boolean isValidMove,
        String position  // "row,col" format
) {
    /**
     * Creates a new CellDTO with validation.
     */
    public CellDTO {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Type cannot be null or blank");
        }
        if (!type.matches("CARD|EMPTY|HOLE")) {
            throw new IllegalArgumentException("Invalid cell type: " + type);
        }
        if (type.equals("CARD") && card == null) {
            throw new IllegalArgumentException("Card type cell must have a card");
        }
        if (!type.equals("CARD") && card != null) {
            throw new IllegalArgumentException("Non-card type cell cannot have a card");
        }
        if (position == null || !position.matches("\\d+,\\d+")) {
            throw new IllegalArgumentException("Invalid position format");
        }
    }

    /**
     * Gets the row coordinate of this cell.
     */
    public int getRow() {
        return Integer.parseInt(position.split(",")[0]);
    }

    /**
     * Gets the column coordinate of this cell.
     */
    public int getCol() {
        return Integer.parseInt(position.split(",")[1]);
    }
}