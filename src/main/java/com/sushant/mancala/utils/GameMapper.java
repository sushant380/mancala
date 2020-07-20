package com.sushant.mancala.utils;

import com.sushant.mancala.controller.GameController;
import com.sushant.mancala.domain.Game;
import com.sushant.mancala.domain.Pit;
import com.sushant.mancala.domain.Player;
import com.sushant.mancala.dto.GameDto;
import com.sushant.mancala.dto.PitDto;
import com.sushant.mancala.dto.PlayerDto;
import org.springframework.hateoas.Link;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/** DTO mapper for Game model. */
public class GameMapper {

  private GameMapper() {}

  /**
   * Convert all games to dtos
   *
   * @param games List of games
   * @return List of dtos
   */
  public static List<GameDto> mapGamesDto(List<Game> games) {
    return games.stream().map(GameMapper::mapDto).collect(Collectors.toList());
  }

  /**
   * Convert model to dto
   *
   * @param game game model
   * @return dto
   */
  public static GameDto mapDto(Game game) {
    GameDto gameDto =
        GameDto.builder()
            ._id(game.get_id())
            .status(game.getStatus())
            .pits(getPits(game.getPits()))
            .nextPlayer(getPlayerDto(game.getNextPlayer()))
            .players(GameMapper.getPlayersDto(game))
            .winnerPlayer(GameMapper.getPlayerDto(game.getWinnerPlayer()))
            .build();
    addLinks(gameDto);
    return gameDto;
  }

  /**
   * Convert pits to dtos.
   *
   * @param pits List of pits
   * @return list of dtos
   */
  private static List<PitDto> getPits(List<Pit> pits) {
    return pits.stream().map(GameMapper::getPit).collect(Collectors.toList());
  }

  /**
   * Convert pit model to dto.
   *
   * @param pit Pit model
   * @return pit dto.
   */
  private static PitDto getPit(Pit pit) {
    return PitDto.builder()
        .pitId(pit.getPitIndex())
        .pables(pit.getPables())
        .mancala(pit.isMancala())
        .build();
  }

  /**
   * Get Players dto from game.
   *
   * @param game game.
   * @return players dto.
   */
  private static PlayerDto[] getPlayersDto(Game game) {
    return Arrays.stream(game.getPlayers()).map(GameMapper::getPlayerDto).toArray(PlayerDto[]::new);
  }

  /**
   * Convert player model to dto.
   *
   * @param player player model.
   * @return player dto.
   */
  private static PlayerDto getPlayerDto(Player player) {
    return player != null ? PlayerDto.builder()._id(player.get_id()).build() : null;
  }

  /**
   * Add links to dto.
   *
   * @param gameDto
   * @return
   */
  private static GameDto addLinks(GameDto gameDto) {
    addSelfLink(gameDto);
    addGameJoinLink(gameDto);
    addPitMoveLink(gameDto);
    return gameDto;
  }

  /**
   * Add self link for game.
   *
   * @param gameDto
   */
  private static void addSelfLink(GameDto gameDto) {
    Link selfLink =
        linkTo(methodOn(GameController.class).getGameById(gameDto.get_id())).withSelfRel();
    gameDto.add(selfLink);
  }

  /**
   * add game join link.
   *
   * @param gameDto
   */
  private static void addGameJoinLink(GameDto gameDto) {
    if (gameDto.getPlayers()[0] == null || gameDto.getPlayers()[1] == null) {
      Link joinLink =
          linkTo(methodOn(GameController.class).join(gameDto.get_id(), null)).withRel("join");
      gameDto.add(joinLink);
    }
  }

  /**
   * add pit move link for all pits.
   *
   * @param gameDto
   */
  private static void addPitMoveLink(GameDto gameDto) {
    gameDto.getPits().stream().forEach(pit -> GameMapper.getPitLink(gameDto, pit));
  }

  /**
   * Add pit move for pit.
   *
   * @param gameDto
   * @param ptoDto
   */
  private static void getPitLink(GameDto gameDto, PitDto ptoDto) {
    ptoDto.add(
        linkTo(methodOn(GameController.class).makeMove(gameDto.get_id(), ptoDto.getPitId(), null))
            .withRel("makeMove"));
  }
}
