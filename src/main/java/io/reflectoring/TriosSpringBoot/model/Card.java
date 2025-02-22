package io.reflectoring.TriosSpringBoot.model;

import io.reflectoring.TriosSpringBoot.model.Direction;
import io.reflectoring.TriosSpringBoot.model.Player;

/**
 * Represents a single card in the game with four directional values and ownership.
 */
public interface Card {
  /**
   * Gets the unique identifier of this card.
   *
   * @return card identifier
   */
  String getIdentifier();

  /**
   * Get the value of the card.
   *
   * @param direction the direction of the card.
   * @return the value of the card based on the direction.
   * @throws IllegalArgumentException if the direction is invalid
   */
  int getValue(Direction direction);

  /**
   * Gets the current owner of this card.
   *
   * @return current owner's color
   */
  Player getOwner();

  /**
   * Changes the ownership of this card.
   *
   * @param newOwner the new owner's color
   * @throws IllegalArgumentException if newOwner is null
   */
  void setOwner(Player newOwner);
}