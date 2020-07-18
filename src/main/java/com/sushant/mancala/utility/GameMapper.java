package com.sushant.mancala.utility;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.sushant.mancala.controller.GameController;
import com.sushant.mancala.domain.Game;
import com.sushant.mancala.dto.GameDto;
import com.sushant.mancala.dto.PitDto;
import com.sushant.mancala.dto.PlayerDto;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

@Component
public class GameMapper {

  public List<GameDto> mapGamesDto(List<Game> games) {
    return games.stream().map(this::mapDto).collect(Collectors.toList());
  }

  public GameDto mapDto(Game game) {
    GameDto gameDto =
        GameDto.builder()
            ._id(game.get_id())
            .status(game.getStatus())
            .pits(
                game.getPits().stream()
                    .map(
                        pit ->
                            PitDto.builder()
                                .pitId(pit.getPitIndex())
                                .pables(pit.getPables())
                                .mancala(pit.isMancala())
                                .build())
                    .collect(Collectors.toList()))
            .nextPlayer(
                game.getNextPlayer() != null
                    ? PlayerDto.builder()._id(game.getNextPlayer().get_id()).build()
                    : null)
            .players(
                Arrays.stream(game.getPlayers())
                    .map(
                        player -> {
                          if (player != null)
                            return PlayerDto.builder()._id(player.get_id()).build();
                          else return null;
                        })
                    .toArray(PlayerDto[]::new))
            .winnerPlayer(
                game.getWinnerPlayer() != null
                    ? PlayerDto.builder()._id(game.getWinnerPlayer().get_id()).build()
                    : null)
            .build();
    addLinks(gameDto);
    return gameDto;
  }

  private GameDto addLinks(GameDto gameDto) {
    addSelfLink(gameDto);
    addGameJoinLink(gameDto);
    addPitMoveLink(gameDto);
    return gameDto;
  }

  private void addSelfLink(GameDto gameDto) {
    Link selfLink =
        linkTo(methodOn(GameController.class).getGameById(gameDto.get_id())).withSelfRel();
    gameDto.add(selfLink);
  }

  private void addGameJoinLink(GameDto gameDto) {
    if (gameDto.getPlayers()[0] == null || gameDto.getPlayers()[1] == null) {
      Link joinLink =
          linkTo(methodOn(GameController.class).join(gameDto.get_id(), null)).withRel("join");
      gameDto.add(joinLink);
    }
  }

  private void addPitMoveLink(GameDto gameDto) {
    gameDto.getPits().stream()
        .forEach(
            pitDto -> {
              Link joinLink =
                  linkTo(
                          methodOn(GameController.class)
                              .makeMov(gameDto.get_id(), pitDto.getPitId(), null))
                      .withRel("makeMove");
              pitDto.add(joinLink);
            });
  }
}
