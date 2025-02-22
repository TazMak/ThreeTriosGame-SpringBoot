package io.reflectoring.TriosSpringBoot.strategy;

import io.reflectoring.TriosSpringBoot.model.Card;
import io.reflectoring.TriosSpringBoot.model.Coordinate;
import lombok.Getter;

/**
 * Represents a move in the Three Trios game.
 */
@Getter
public class Move {
    /**
     * -- GETTER --
     *  Gets the card of the move.
     *
     * @return the card
     */
    private final Card card;
    /**
     * -- GETTER --
     *  Gets the position of the move.
     *
     * @return the position
     */
    private final Coordinate position;
    /**
     * -- GETTER --
     *  Gets the value of the move.
     *
     * @return the value
     */
    private final int value; // For comparing moves

  /**
   * Constructor for the Move class.
   *
   * @param card the card to play
   * @param position the position to play the card
   * @param value the value of the move
   */
  public Move(Card card, Coordinate position, int value) {
    this.card = card;
    this.position = position;
    this.value = value;

    if (card == null || position == null) {
      throw new IllegalArgumentException("Card and position must be non-null");
    }

    if (value < 0) {
      throw new IllegalArgumentException("Value must be non-negative");
    }
  }

}