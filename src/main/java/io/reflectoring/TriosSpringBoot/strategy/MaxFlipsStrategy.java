package io.reflectoring.TriosSpringBoot.strategy;

import io.reflectoring.TriosSpringBoot.model.Card;
import io.reflectoring.TriosSpringBoot.model.Coordinate;
import io.reflectoring.TriosSpringBoot.model.Direction;
import io.reflectoring.TriosSpringBoot.model.GameState;
import io.reflectoring.TriosSpringBoot.model.PlayerColor;
import io.reflectoring.TriosSpringBoot.model.ReadOnlyThreeTriosModel;
import java.util.List;

/**
 * Strategy that chooses the move with the most flips.
 */
public class MaxFlipsStrategy implements Strategy {

  @Override
  public Move chooseMove(ReadOnlyThreeTriosModel model, PlayerColor player) {
    if (model.getGameState() == GameState.GAME_OVER) {
      return null;
    }
    List<Card> hand = model.getPlayerHand(player);
    List<Coordinate> emptyCells = model.getBoard().getEmptyCardCells();
    Move bestMove = null;
    int maxFlips = -1;

    for (Card card : hand) {
      for (Coordinate pos : emptyCells) {
        if (model.getBoard().canPlaceCard(pos)) {
          int flips = model.getPotentialFlips(card, pos);
          if (flips > maxFlips
                  || (flips == maxFlips
                  && shouldPreferMove(bestMove, card, pos, model, player))) {
            maxFlips = flips;
            bestMove = new Move(card, pos, flips);
          }
        }
      }
    }

    return bestMove;
  }

  private boolean shouldPreferMove(Move currentBest, Card newCard, Coordinate newPos,
                                   ReadOnlyThreeTriosModel model, PlayerColor player) {
    if (currentBest == null) {
      return true;
    }

    // Prefer higher value cards (assuming average of directional values indicates strength)
    double currentCardStrength = getCardStrength(currentBest.getCard());
    double newCardStrength = getCardStrength(newCard);
    return newCardStrength > currentCardStrength;
  }

  private double getCardStrength(Card card) {
    return (card.getValue(Direction.NORTH)
            + card.getValue(Direction.SOUTH)
            + card.getValue(Direction.EAST)
            + card.getValue(Direction.WEST)) / 4.0;
  }
}