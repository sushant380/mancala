package com.sushant.mancala.controller;

import com.sushant.mancala.dto.GameDto;
import com.sushant.mancala.service.GameService;

import java.util.List;
import javax.inject.Inject;
import javax.validation.constraints.Min;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/games")
@Validated
public class GameController {

  @Inject private GameService gameService;

  @PostMapping(produces = "application/json")
  public ResponseEntity createGame(@Parameter(hidden = true) @AuthenticationPrincipal User user) {
    GameDto game = gameService.createAndJoinGame(user.getUsername());
    return new ResponseEntity(game, HttpStatus.CREATED);
  }

  @GetMapping(produces = "application/json")
  public ResponseEntity getGames() {
    List<GameDto> games = gameService.getGames();
    return new ResponseEntity(games, HttpStatus.OK);
  }

  @GetMapping(path = "/{gameId}", produces = "application/json")
  public ResponseEntity getGameById(@PathVariable(value = "gameId") String gameId) {
    GameDto game = gameService.findById(gameId);
    return new ResponseEntity(game, HttpStatus.OK);
  }

  @PutMapping(path = "/{gameId}/join", produces = "application/json")
  public ResponseEntity join(
          @PathVariable(value = "gameId") String gameId,
          @Parameter(hidden = true) @AuthenticationPrincipal User user) {
    GameDto game = gameService.joinGame(gameId, user.getUsername());
    return new ResponseEntity(game, HttpStatus.OK);
  }

  @PutMapping(path = "/{gameId}/pits/{pitId}", produces = "application/json")
  public ResponseEntity makeMove(
      @PathVariable(value = "gameId") String gameId,
      @Min(value = 1) @PathVariable(value = "pitId") int pitId,
      @Parameter(hidden = true) @AuthenticationPrincipal User user) {
    GameDto game = gameService.makeMove(gameId, user.getUsername(), pitId);
    return new ResponseEntity(game, HttpStatus.OK);
  }

  @PreAuthorize("hasAnyAuthority('ADMIN')")
  @DeleteMapping(path = "/{gameId}", produces = "application/json")
  public void delete(
      @PathVariable(value = "gameId") String gameId,
      @Parameter(hidden = true) @AuthenticationPrincipal User user) {
    gameService.delete(gameId);
  }
}
