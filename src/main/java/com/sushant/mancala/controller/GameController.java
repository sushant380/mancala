package com.sushant.mancala.controller;

import com.sushant.mancala.dto.GameDto;
import com.sushant.mancala.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import javax.inject.Inject;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/games")
@Validated
@SecurityRequirement(name = "basicAuth")
@Tag(name = "Games", description = "API for Mancala game")
public class GameController {

  @Inject private GameService gameService;

  @Operation(
      summary = "Create new game and join",
      description =
          "Create new game and the user will get registered for the game as player. "
          + "Use authorization header for player access. "
          + "Username is player id used of registration")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Game is created",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = GameDto.class))
            }),
        @ApiResponse(
            responseCode = "401",
            description = "Your are not authorized player",
            content = @Content)
      })
  @PostMapping(produces = "application/json")
  public ResponseEntity createGame(@Parameter(hidden = true) @AuthenticationPrincipal User user) {
    GameDto game = gameService.createAndJoinGame(user.getUsername());
    return new ResponseEntity(game, HttpStatus.CREATED);
  }

  @Operation(summary = "Get list of all games", description = "List of games and their states")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "All games fetched",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = GameDto.class))
            }),
        @ApiResponse(
            responseCode = "401",
            description = "Your are not authorized player",
            content = @Content)
      })
  @GetMapping(produces = "application/json")
  public ResponseEntity getGames() {
    List<GameDto> games = gameService.getGames();
    return new ResponseEntity(games, HttpStatus.OK);
  }

  @Operation(summary = "Get game by ID", description = "Get Specific game based on game id")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Game fetched",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = GameDto.class))
            }),
        @ApiResponse(responseCode = "404", description = "Game not found", content = @Content),
        @ApiResponse(
            responseCode = "401",
            description = "Your are not authorized player",
            content = @Content)
      })
  @GetMapping(path = "/{gameId}", produces = "application/json")
  public ResponseEntity getGameById(
      @Parameter(description = "Game ID", example = "Ahasd8adsn2394")
          @PathVariable(value = "gameId")
          String gameId) {
    GameDto game = gameService.findById(gameId);
    return new ResponseEntity(game, HttpStatus.OK);
  }

  @Operation(
      summary = "Join the game",
      description = "Join the existing game created by other player")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Player joined the game",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = GameDto.class))
            }),
        @ApiResponse(responseCode = "404", description = "Game not found", content = @Content),
        @ApiResponse(
            responseCode = "400",
            description = "You cannot join the game",
            content = @Content),
        @ApiResponse(
            responseCode = "401",
            description = "Your are not authorized player",
            content = @Content)
      })
  @PutMapping(path = "/{gameId}/join", produces = "application/json")
  public ResponseEntity join(
      @Parameter(description = "Game ID", example = "Ahasd8adsn2394")
          @PathVariable(value = "gameId")
          String gameId,
      @Parameter(hidden = true) @AuthenticationPrincipal User user) {
    GameDto game = gameService.joinGame(gameId, user.getUsername());
    return new ResponseEntity(game, HttpStatus.OK);
  }

  @Operation(summary = "Make a move", description = "Player makes a move by selecting pit")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Player joined the game",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = GameDto.class))
            }),
        @ApiResponse(responseCode = "404", description = "Game not found", content = @Content),
        @ApiResponse(responseCode = "403", description = "Invalid move.", content = @Content),
        @ApiResponse(
            responseCode = "401",
            description = "Your are not authorized player",
            content = @Content)
      })
  @PutMapping(path = "/{gameId}/pits/{pitId}", produces = "application/json")
  public ResponseEntity makeMove(
      @Parameter(description = "Game ID", example = "Ahasd8adsn2394")
          @PathVariable(value = "gameId")
          String gameId,
      @Parameter(description = "Pit id. Non negative number", example = "1")
          @Min(value = 1)
          @PathVariable(value = "pitId")
          int pitId,
      @Parameter(hidden = true) @AuthenticationPrincipal User user) {
    GameDto game = gameService.makeMove(gameId, user.getUsername(), pitId);
    return new ResponseEntity(game, HttpStatus.OK);
  }

  @Operation(
      summary = "Delete game",
      description = "Player with admin rights can delete the game from system")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Game deleted", content = @Content),
        @ApiResponse(responseCode = "404", description = "Game not found", content = @Content),
        @ApiResponse(
            responseCode = "401",
            description = "Your are not authorized player",
            content = @Content)
      })
  @PreAuthorize("hasAnyAuthority('ADMIN')")
  @DeleteMapping(path = "/{gameId}", produces = "application/json")
  public void delete(
      @Parameter(description = "Game ID", example = "Ahasd8adsn2394")
          @PathVariable(value = "gameId")
          String gameId,
      @Parameter(hidden = true) @AuthenticationPrincipal User user) {
    gameService.delete(gameId);
  }
}
