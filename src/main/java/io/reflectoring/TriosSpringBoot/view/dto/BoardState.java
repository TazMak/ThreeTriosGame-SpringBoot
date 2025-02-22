package io.reflectoring.TriosSpringBoot.view.dto;

import java.util.List;

/**
 * DTO representing the complete state of the game board.
 */
public record BoardState(
        int rows,
        int cols,
        List<List<CellDTO>> cells,
        List<List<Boolean>> validMoves,
        String selectedPosition  // Format: "row,col" or null if no selection
) {
    /**
     * Creates a new BoardState with validation.
     */
    public BoardState {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Board dimensions must be positive");
        }
        if (cells == null || cells.isEmpty() || cells.size() != rows) {
            throw new IllegalArgumentException("Invalid cells array");
        }
        for (var row : cells) {
            if (row == null || row.size() != cols) {
                throw new IllegalArgumentException("Invalid row in cells array");
            }
        }
        if (validMoves != null) {
            if (validMoves.size() != rows) {
                throw new IllegalArgumentException("Invalid validMoves array");
            }
            for (var row : validMoves) {
                if (row == null || row.size() != cols) {
                    throw new IllegalArgumentException("Invalid row in validMoves array");
                }
            }
        }
    }

    /**
     * Gets the cell at the specified position.
     */
    public CellDTO getCellAt(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            throw new IllegalArgumentException("Invalid position");
        }
        return cells.get(row).get(col);
    }

    /**
     * Checks if a move is valid at the specified position.
     */
    public boolean isValidMove(int row, int col) {
        if (validMoves == null) return false;
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return false;
        }
        return validMoves.get(row).get(col);
    }
}