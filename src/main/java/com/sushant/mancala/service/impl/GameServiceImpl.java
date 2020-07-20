package com.sushant.mancala.service.impl;

import com.sushant.mancala.common.GameStatus;
import com.sushant.mancala.domain.Game;
import com.sushant.mancala.dto.GameDto;
import com.sushant.mancala.exception.GameNotFoundException;
import com.sushant.mancala.exception.InvalidGameException;
import com.sushant.mancala.handler.GameHandler;
import com.sushant.mancala.repository.GameRepository;
import com.sushant.mancala.service.GameService;
import com.sushant.mancala.utils.GameMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/** Game service implementation. */
@Service
public class GameServiceImpl implements GameService {

  private final Logger LOGGER= LoggerFactory.getLogger(GameServiceImpl.class);

  @Inject private GameRepository gameRepository;

  @Value("${mancala.pits}")
  private int pits;

  @Value("${mancala.pables}")
  private int pables;

  @Inject private GameHandler gameHandler;

  @Override
  public List<GameDto> getGames() {
    return GameMapper.mapGamesDto(gameRepository.findAll());
  }

  @Override
  public GameDto createAndJoinGame(String playerId) {
    LOGGER.info("Creating game with {} pits and {} pables per pit",pits,pables);
    Game game = gameHandler.getGame(pits, pables);
    LOGGER.info("{} joining game",playerId);
    gameHandler.joinGame(game, playerId);
    gameRepository.save(game);
    return GameMapper.mapDto(game);
  }

  @Override
  public GameDto    joinGame(String gameId, String playerId) {
    Optional<Game> gameOption = gameRepository.findById(gameId);
    if (gameOption.isPresent()) {
      Game game = gameOption.get();
      LOGGER.info("Game status is {}",game.getStatus());
      if (game.getStatus() == GameStatus.CREATED) {
        LOGGER.info("{} joining game",playerId);
        gameHandler.joinGame(game, playerId);
        gameRepository.save(game);
        return GameMapper.mapDto(game);
      } else {
        LOGGER.error("{} cannot join game",playerId);
        throw new InvalidGameException();
      }
    } else {
      LOGGER.error("{} game not found",gameId);
      throw new GameNotFoundException();
    }
  }

  @Override
  public GameDto makeMove(String gameId, String playerId, int pitId) {
    Optional<Game> gameOption = gameRepository.findById(gameId);
    if (gameOption.isPresent()) {
      Game game = gameOption.get();
      LOGGER.info("Moving pables in pit {} for game {} ",pitId,gameId);
      gameHandler.move(game, playerId, pitId);
      gameRepository.save(game);
      return GameMapper.mapDto(game);
    } else {
      LOGGER.error("{} game not found",gameId);
      throw new GameNotFoundException();
    }
  }

  @Override
  public GameDto findById(String gameId) {
    Optional<Game> gameOption = gameRepository.findById(gameId);
    if (gameOption.isPresent()) {
      return GameMapper.mapDto(gameOption.get());
    }
    throw new GameNotFoundException();
  }

  @Override
  public void delete(String gameId) {
    Optional<Game> gameOptional = gameRepository.findById(gameId);
    if (gameOptional.isPresent()) {
      LOGGER.warn("Deleting game {} ",gameId);
      gameRepository.delete(gameOptional.get());
    } else {
      LOGGER.error("{} game not found",gameId);
      throw new GameNotFoundException();
    }
  }
}
