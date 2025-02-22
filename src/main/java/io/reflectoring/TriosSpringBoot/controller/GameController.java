package io.reflectoring.TriosSpringBoot.controller;

import io.reflectoring.TriosSpringBoot.entity.Game;
import io.reflectoring.TriosSpringBoot.model.PlayerColor;
import io.reflectoring.TriosSpringBoot.service.GameService;
import io.reflectoring.TriosSpringBoot.view.dto.*;
import io.reflectoring.TriosSpringBoot.exception.GameNotFoundException;
import io.reflectoring.TriosSpringBoot.exception.InvalidMoveException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.Data;
import java.util.List;

@RestController
@RequestMapping("/api/games")
public class GameController {
  @Autowired
  private GameService gameService;

  @PostMapping
  public ResponseEntity<GameResponseDTO> createGame(@RequestBody CreateGameRequest request) {
    Game game = gameService.createGame(
            request.getGridConfig(),
            request.getCardConfig(),
            request.getRedPlayerType(),
            request.getBluePlayerType()
    );
    return ResponseEntity.ok(new GameResponseDTO(game));
  }

  @PostMapping("/{gameId}/start")
  public ResponseEntity<GameStateDTO> startGame(@PathVariable Long gameId) {
    try {
      GameStateDTO state = gameService.getGameState(gameId);
      return ResponseEntity.ok(state);
    } catch (GameNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @PostMapping("/{gameId}/move")
  public ResponseEntity<GameStateDTO> makeMove(
          @PathVariable Long gameId,
          @RequestBody MoveRequestDTO request) {
    try {
      GameStateDTO state = gameService.playCard(
              gameId,
              request.getPlayerColor().toString(),
              request.getCardId(),
              request.getPosition()
      );
      return ResponseEntity.ok(state);
    } catch (InvalidMoveException e) {
      return ResponseEntity.badRequest().body(null);
    } catch (GameNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/{gameId}")
  public ResponseEntity<GameStateDTO> getGameState(@PathVariable Long gameId) {
    try {
      GameStateDTO state = gameService.getGameState(gameId);
      return ResponseEntity.ok(state);
    } catch (GameNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping
  public ResponseEntity<List<GameResponseDTO>> listGames() {
    return ResponseEntity.ok(gameService.listActiveGames());
  }
}

@Data
class CreateGameRequest {
  private String gridConfig;
  private String cardConfig;
  private String redPlayerType;
  private String bluePlayerType;
}