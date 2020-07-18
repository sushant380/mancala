package com.sushant.mancala.controller;

import com.sushant.mancala.domain.Game;
import com.sushant.mancala.dto.GameDto;
import com.sushant.mancala.service.GameService;
import com.sushant.mancala.utility.LinkResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/games")
public class GameController {

  @Inject private GameService gameService;

  private LinkResolver linkResolver=new LinkResolver();
  @PostMapping(produces = "application/json")
  public ResponseEntity createGame(@ApiIgnore @AuthenticationPrincipal User user) {
    Game game = gameService.createGame(user.getUsername());
    return new ResponseEntity(linkResolver.addLinks(game), HttpStatus.CREATED);
  }

  @GetMapping(produces = "application/json")
  public ResponseEntity getGames() {
    List<Game> games = gameService.getGames();
    List<GameDto> gamesDtoList = games.stream().map(game ->linkResolver.addLinks(game)).collect(Collectors.toList());
    return new ResponseEntity(gamesDtoList, HttpStatus.OK);
  }

  @GetMapping(path = "/{gameId}", produces = "application/json")
  public ResponseEntity getGameById(@PathVariable(value = "gameId") String gameId) {
    Game game = gameService.findById(gameId);
    return new ResponseEntity(linkResolver.addLinks(game), HttpStatus.OK);
  }

  @PutMapping(path = "/{gameId}/pits/{pitId}", produces = "application/json")
  public ResponseEntity makeMov(
      @PathVariable(value = "gameId") String gameId,
      @PathVariable(value = "pitId") int pitId,
      @ApiIgnore @AuthenticationPrincipal User user) {
    Game game = gameService.makeMove(gameId, user.getUsername(), pitId);
    return new ResponseEntity(linkResolver.addLinks(game), HttpStatus.OK);
  }

  @PostMapping(path = "/{gameId}/join", produces = "application/json")
  public ResponseEntity join(
      @PathVariable(value = "gameId") String gameId,
      @ApiIgnore @AuthenticationPrincipal User user) {
    Game game = gameService.joinGame(gameId, user.getUsername());

    return new ResponseEntity(linkResolver.addLinks(game), HttpStatus.OK);
  }

  @PreAuthorize("hasAnyAuthority('ADMIN')")
  @DeleteMapping(path = "/{gameId}", produces = "application/json")
  public void delete(@PathVariable(value = "gameId") String gameId,
                     @ApiIgnore @AuthenticationPrincipal User user){
    gameService.delete(gameId);
  }
}
