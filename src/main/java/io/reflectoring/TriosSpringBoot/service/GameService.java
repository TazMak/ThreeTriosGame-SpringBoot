package io.reflectoring.TriosSpringBoot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.transaction.annotation.Transactional;
import io.reflectoring.TriosSpringBoot.entity.Game;
import io.reflectoring.TriosSpringBoot.entity.Player;
import io.reflectoring.TriosSpringBoot.model.*;
import io.reflectoring.TriosSpringBoot.repository.GameRepository;
import io.reflectoring.TriosSpringBoot.repository.PlayerRepository;
import io.reflectoring.TriosSpringBoot.view.dto.*;
import io.reflectoring.TriosSpringBoot.exception.*;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class GameService {
    private record PlayerHandUpdate(String color, String playerType) {}

    private sealed interface GameAction permits CreateGame, PlayMove, CancelMove {
        GameStateDTO execute(ThreeTriosModel model);
    }

    private record CreateGame(String redPlayerType, String bluePlayerType) implements GameAction {
        @Override
        public GameStateDTO execute(ThreeTriosModel model) {
            ((AIThreeTriosGame) model).initializePlayersWithTypes(redPlayerType, bluePlayerType);
            model.startGame();
            return null;
        }
    }

    private record PlayMove(PlayerColor player, Card card, Coordinate position) implements GameAction {
        @Override
        public GameStateDTO execute(ThreeTriosModel model) {
            model.playCard(card, position);
            return null;
        }
    }

    private record CancelMove(PlayerColor player) implements GameAction {
        @Override
        public GameStateDTO execute(ThreeTriosModel model) {
            return null;
        }
    }

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final ObjectMapper objectMapper;
    private final Map<Long, ThreeTriosModel> activeGames;

    public GameService(GameRepository gameRepository, PlayerRepository playerRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.objectMapper = new ObjectMapper();
        this.activeGames = new ConcurrentHashMap<>();
    }

    @Transactional
    public Game createGame(String gridConfig, String cardConfig, String redPlayerType, String bluePlayerType) {
        var gameModel = new AIThreeTriosGame();
        gameModel.initializeGameFromFiles(gridConfig, cardConfig);

        var action = new CreateGame(redPlayerType, bluePlayerType);
        action.execute(gameModel);

        var game = Game.builder()
                .gameState(GameState.INITIALIZATION.name())
                .currentPlayer(PlayerColor.RED.name())
                .boardConfig(gridConfig)
                .createdAt(LocalDateTime.now())
                .isActive(true)
                .build();

        game = gameRepository.save(game);
        createPlayers(game, redPlayerType, bluePlayerType);
        activeGames.put(game.getId(), gameModel);

        return game;
    }

    private void createPlayers(Game game, String redPlayerType, String bluePlayerType) {
        var players = List.of(
                new PlayerHandUpdate(PlayerColor.RED.name(), redPlayerType),
                new PlayerHandUpdate(PlayerColor.BLUE.name(), bluePlayerType)
        );

        players.forEach(update -> createPlayer(game, update.color(), update.playerType()));
    }

    private void createPlayer(Game game, String color, String playerType) {
        var player = Player.builder()
                .color(color)
                .hand("[]")
                .game(game)
                .playerType(playerType)
                .build();
        playerRepository.save(player);
    }

    @Transactional
    public GameStateDTO playCard(Long gameId, String playerColor, String cardId, CoordinateDTO position) {
        var gameModel = getGameModel(gameId);
        var currentPlayer = PlayerColor.valueOf(playerColor);

        if (gameModel.getCurrentPlayerColor() != currentPlayer) {
            throw new InvalidMoveException("Not your turn");
        }

        var cardToPlay = findCardInHand(gameModel, currentPlayer, cardId);
        var gamePosition = new GameCoordinate(position.row(), position.col());

        var action = new PlayMove(currentPlayer, cardToPlay, gamePosition);
        action.execute(gameModel);

        updateGameState(gameId, gameModel);
        checkAndExecuteAIMove(gameId);

        return convertToGameStateDTO(gameModel, gameId);
    }

    private void checkAndExecuteAIMove(Long gameId) {
        var gameModel = getGameModel(gameId);
        var game = findGame(gameId);

        var currentPlayer = playerRepository.findByGameId(gameId)
                .stream()
                .filter(p -> p.getColor().equals(gameModel.getCurrentPlayerColor().name()))
                .findFirst()
                .orElseThrow(() -> new GameNotFoundException("Player not found"));

        if (currentPlayer.getPlayerType().equals("AI") &&
                gameModel.getGameState() == GameState.WAITING_FOR_MOVE) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            var modelPlayer = ((AIThreeTriosGame) gameModel).getCurrentPlayer();
            if (modelPlayer instanceof AICapable aiPlayer) {
                aiPlayer.makeMove();
            } else {
                throw new IllegalStateException("Current player is marked as AI but does not implement AICapable");
            }

            updateGameState(gameId, gameModel);
        }
    }

    private void updateGameState(Long gameId, ThreeTriosModel gameModel) {
        var game = findGame(gameId);
        game.setGameState(gameModel.getGameState().name());
        game.setCurrentPlayer(gameModel.getCurrentPlayerColor().name());
        gameRepository.save(game);

        updatePlayerHands(gameId, gameModel);
    }

    private Card findCardInHand(ThreeTriosModel model, PlayerColor player, String cardId) {
        return model.getPlayerHand(player).stream()
                .filter(card -> card.getIdentifier().equals(cardId))
                .findFirst()
                .orElseThrow(() -> new InvalidMoveException("Card not found: " + cardId));
    }

    private void updatePlayerHands(Long gameId, ThreeTriosModel gameModel) {
        var players = playerRepository.findByGameId(gameId);
        for (var player : players) {
            try {
                var color = PlayerColor.valueOf(player.getColor());
                var hand = gameModel.getPlayerHand(color);
                player.setHand(objectMapper.writeValueAsString(hand));
                playerRepository.save(player);
            } catch (Exception e) {
                throw new RuntimeException("Error updating player hand", e);
            }
        }
    }

    private GameStateDTO convertToGameStateDTO(ThreeTriosModel model, Long gameId) {
        var board = model.getBoard();
        var boardState = createBoardState(board);

        return new GameStateDTO(
                String.valueOf(gameId),
                model.getGameState().name(),
                model.getCurrentPlayerColor(),
                boardState,
                model.getPlayerHand(PlayerColor.RED).stream()
                        .map(this::convertToCardDTO)
                        .toList(),
                model.getPlayerHand(PlayerColor.BLUE).stream()
                        .map(this::convertToCardDTO)
                        .toList(),
                model.getScore(PlayerColor.RED),
                model.getScore(PlayerColor.BLUE),
                model.getGameState() == GameState.GAME_OVER ? model.getWinner().name() : null,
                null
        );
    }

    private BoardState createBoardState(Board board) {
        var cells = new ArrayList<List<CellDTO>>();
        var rows = board.getGrid().getTotalRows();
        var cols = board.getGrid().getTotalColumns();

        for (int i = 0; i < rows; i++) {
            var row = new ArrayList<CellDTO>();
            for (int j = 0; j < cols; j++) {
                var pos = new GameCoordinate(i, j);
                row.add(createCellDTO(board, pos));
            }
            cells.add(row);
        }

        return new BoardState(rows, cols, cells, Collections.emptyList(), Collections.emptyList().toString());
    }

    private CellDTO createCellDTO(Board board, Coordinate pos) {
        var cellState = board.getGrid().getCellState(pos);
        return switch (cellState) {
            case HOLE -> new CellDTO("HOLE", null, false, pos.getRow() + "," + pos.getCol());
            case OCCUPIED -> {
                var card = board.getCardAt(pos);
                yield new CellDTO("CARD", convertToCardDTO(card), false,
                        pos.getRow() + "," + pos.getCol());
            }
            case AVAILABLE -> new CellDTO("EMPTY", null, board.canPlaceCard(pos),
                    pos.getRow() + "," + pos.getCol());
        };
    }

    private CardDTO convertToCardDTO(Card card) {
        var values = Map.of(
                "NORTH", card.getValue(Direction.NORTH),
                "SOUTH", card.getValue(Direction.SOUTH),
                "EAST", card.getValue(Direction.EAST),
                "WEST", card.getValue(Direction.WEST)
        );

        return new CardDTO(
                card.getIdentifier(),
                card.getOwner().getColor().name(),
                values
        );
    }

    private ThreeTriosModel getGameModel(Long gameId) {
        return Optional.ofNullable(activeGames.get(gameId))
                .orElseThrow(() -> new GameNotFoundException("Game not found: " + gameId));
    }

    private Game findGame(Long gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException("Game not found: " + gameId));
    }

    public GameStateDTO getGameState(Long gameId) {
        return convertToGameStateDTO(getGameModel(gameId), gameId);
    }

    public List<GameResponseDTO> listActiveGames() {
        return gameRepository.findByIsActiveTrue().stream()
                .map(GameResponseDTO::new)
                .toList();
    }
}