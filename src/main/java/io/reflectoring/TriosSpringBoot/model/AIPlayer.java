package io.reflectoring.TriosSpringBoot.model;

import io.reflectoring.TriosSpringBoot.strategy.Move;
import io.reflectoring.TriosSpringBoot.strategy.Strategy;
import io.reflectoring.TriosSpringBoot.exception.InvalidMoveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * AI implementation of Player that automatically makes moves using a strategy.
 */
public class AIPlayer implements Player, AICapable {
  private static final Logger logger = LoggerFactory.getLogger(AIPlayer.class);

  private final PlayerColor color;
  private final List<Card> hand;
  private final Strategy strategy;
  private final ThreeTriosModel gameModel;

  /**
   * Creates a new AI player with the specified color and strategy.
   *
   * @param color the player's color
   * @param strategy the strategy to use for moves
   * @param gameModel the game model reference for making moves
   * @throws IllegalArgumentException if any parameter is null
   */
  public AIPlayer(PlayerColor color, Strategy strategy, ThreeTriosModel gameModel) {
    if (color == null || strategy == null || gameModel == null) {
      throw new IllegalArgumentException("Parameters cannot be null");
    }
    this.color = color;
    this.strategy = strategy;
    this.gameModel = gameModel;
    this.hand = new ArrayList<>();
  }

  @Override
  public PlayerColor getColor() {
    return color;
  }

  @Override
  public List<Card> getHand() {
    return Collections.unmodifiableList(hand);
  }

  @Override
  public void addCardToHand(Card card) {
    if (card == null) {
      throw new IllegalArgumentException("Card cannot be null");
    }
    hand.add(card);
    logger.debug("Added card {} to AI player {}'s hand", card.getIdentifier(), color);
  }

  @Override
  public void removeCardFromHand(Card card) {
    if (card == null) {
      throw new IllegalArgumentException("Card cannot be null");
    }
    if (!hand.contains(card)) {
      throw new IllegalArgumentException("Card not in hand: " + card.getIdentifier());
    }
    hand.remove(card);
    logger.debug("Removed card {} from AI player {}'s hand", card.getIdentifier(), color);
  }

  @Override
  public Strategy getStrategy() {
    return strategy;
  }

  /**
   * Makes an automatic move using the AI strategy.
   *
   * @return true if a move was made successfully, false otherwise
   * @throws InvalidMoveException if the selected move is invalid
   */
  @Override
  public boolean makeMove() {
    validateGameState();

    try {
      Move move = selectMove();
      if (move == null) {
        logger.warn("AI strategy returned null move for player {}", color);
        return false;
      }

      executeMove(move);
      return true;

    } catch (Exception e) {
      logger.error("AI Move failed for player {}: {}", color, e.getMessage(), e);
      return false;
    }
  }

  private void validateGameState() {
    if (gameModel.getCurrentPlayerColor() != color) {
      logger.warn("Not {}'s turn to move", color);
      throw new InvalidMoveException("Not your turn");
    }

    if (gameModel.getGameState() != GameState.WAITING_FOR_MOVE) {
      logger.warn("Invalid game state for move: {}", gameModel.getGameState());
      throw new InvalidMoveException("Game not in correct state for move");
    }
  }

  private Move selectMove() {
    Move move = strategy.chooseMove(gameModel, color);
    if (move != null) {
      validateMove(move);
    }
    return move;
  }

  private void validateMove(Move move) {
    Card selectedCard = move.getCard();
    Coordinate selectedPosition = move.getPosition();

    if (!hand.contains(selectedCard)) {
      logger.error("AI attempted to play card not in hand: {}", selectedCard.getIdentifier());
      throw new InvalidMoveException("Selected card not in hand");
    }

    if (!gameModel.getBoard().canPlaceCard(selectedPosition)) {
      logger.error("AI attempted invalid position: {}", selectedPosition);
      throw new InvalidMoveException("Invalid position selected");
    }
  }

  private void executeMove(Move move) {
    logger.info("AI {} playing card {} at position {}",
            color, move.getCard().getIdentifier(), move.getPosition());
    gameModel.playCard(move.getCard(), move.getPosition());
  }
}