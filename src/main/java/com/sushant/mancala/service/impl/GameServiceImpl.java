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
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GameServiceImpl implements GameService {

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
    Game game = gameHandler.getGame(pits, pables);
    gameHandler.joinGame(game, playerId);
    gameRepository.save(game);
    return GameMapper.mapDto(game);
  }

  @Override
  public GameDto joinGame(String gameId, String playerId) {
    Optional<Game> gameOption = gameRepository.findById(gameId);
    if (gameOption.isPresent()) {
      Game game = gameOption.get();
      if (game.getStatus() == GameStatus.CREATED) {
        gameHandler.joinGame(game, playerId);
        gameRepository.save(game);
        return GameMapper.mapDto(game);
      } else {
        throw new InvalidGameException();
      }
    } else {
      throw new GameNotFoundException();
    }
  }

  @Override
  public GameDto makeMove(String gameId, String playerId, int pitId) {
    Optional<Game> gameOption = gameRepository.findById(gameId);
    if (gameOption.isPresent()) {
      Game game = gameOption.get();
      gameHandler.move(game, playerId, pitId);
      gameRepository.save(game);
      return GameMapper.mapDto(game);
    } else {
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
      gameRepository.delete(gameOptional.get());
    } else {
      throw new GameNotFoundException();
    }
  }
}
