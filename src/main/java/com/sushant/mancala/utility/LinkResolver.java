package com.sushant.mancala.utility;

import com.sushant.mancala.controller.GameController;
import com.sushant.mancala.domain.Game;
import com.sushant.mancala.dto.GameDto;
import com.sushant.mancala.dto.PitDto;
import com.sushant.mancala.dto.PlayerDto;
import org.springframework.hateoas.Link;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class LinkResolver {
  public GameDto addLinks(Game game) {
    GameDto gameDto = builder(game);
    addSelfLink(gameDto);
    addGameJoinLink(gameDto);
    addPitMoveLink(gameDto);
    return gameDto;
  }

  public GameDto builder(Game game) {
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
            .nextPlayer(game.getNextPlayer()!=null?PlayerDto.builder()._id(game.getNextPlayer().get_id()).build():null)
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
    return gameDto;
  }

  public void addSelfLink(GameDto gameDto) {
    Link selfLink =
        linkTo(methodOn(GameController.class).getGameById(gameDto.get_id())).withSelfRel();
    gameDto.add(selfLink);
  }

  public void addGameJoinLink(GameDto gameDto) {
    if (gameDto.getPlayers()[0] == null || gameDto.getPlayers()[1] == null) {
      Link joinLink =
          linkTo(methodOn(GameController.class).join(gameDto.get_id(), null)).withRel("join");
      gameDto.add(joinLink);
    }
  }

  public void addPitMoveLink(GameDto gameDto) {
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
