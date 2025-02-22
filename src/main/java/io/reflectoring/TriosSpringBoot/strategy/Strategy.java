package io.reflectoring.TriosSpringBoot.strategy;

import io.reflectoring.TriosSpringBoot.model.PlayerColor;
import io.reflectoring.TriosSpringBoot.model.ReadOnlyThreeTriosModel;

/**
 * Interface for a game playing strategy.
 */
public interface Strategy {
  /**
   * Chooses the best move for the given player.
   *
   * @return Move to make, or null if no valid moves.
   */
  Move chooseMove(ReadOnlyThreeTriosModel model, PlayerColor player);
}