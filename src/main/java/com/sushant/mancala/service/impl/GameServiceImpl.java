package com.sushant.mancala.service.impl;

import com.sushant.mancala.domain.Game;
import com.sushant.mancala.domain.GameStatus;
import com.sushant.mancala.domain.Pit;
import com.sushant.mancala.domain.Player;
import com.sushant.mancala.exception.GameNotFoundException;
import com.sushant.mancala.exception.InvalidGameException;
import com.sushant.mancala.exception.InvalidPlayerMoveException;
import com.sushant.mancala.repository.GameRepository;
import com.sushant.mancala.service.GameService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class GameServiceImpl implements GameService {

  @Inject private GameRepository gameRepository;

  @Override
  public List<Game> getGames() {
    return gameRepository.findAll();
  }

  @Override
  public Game createGame(String playerId) {
    Game game = new Game();
    game.joinGame(playerId);
    gameRepository.save(game);
    return game;
  }

  @Override
  public Game joinGame(String gameId, String playerId) {
    Optional<Game> gameOption = gameRepository.findById(gameId);
    if (gameOption.isPresent()) {
      Game game = gameOption.get();
      if(game.getStatus()==GameStatus.CREATED){
          game.joinGame(playerId);
          gameRepository.save(game);
          return game;
      }else{
          throw new InvalidGameException();
      }
    } else {
      throw new GameNotFoundException();
    }
  }

  @Override
  public Game makeMove(String gameId, String playerId, int pitId) {
    Optional<Game> gameOption = gameRepository.findById(gameId);
    if (gameOption.isPresent()) {
      Game game = gameOption.get();
      Player currentPlayer = game.getPlayerById(playerId);
      if (game.getNextPlayer() != null && !game.getNextPlayer().equals(currentPlayer)) {
        throw new InvalidPlayerMoveException();
      }
      Pit currentPit = game.getPit(pitId);
      int pables = currentPit.peek();
      for (int i = 0; i < pables; i++) {
        Pit nextPit = game.getPit(currentPit.getNextPit());
        // do not put into opposite players mancala
        if (nextPit.isMancala() && !nextPit.equals(game.getPit(currentPlayer.getMancalaPit()))) {
          nextPit=game.getPit(nextPit.getNextPit());
        }
        nextPit.put();
        currentPit = nextPit;

      }
      // Rule 1 check if last pable is in current players mancala
      if (currentPit .equals(game.getPit(currentPlayer.getMancalaPit()))) {
        game.setNextPlayer(currentPlayer);
      }else{
        game.setNextPlayer(Arrays.stream(game.getPlayers()).filter(player -> player.get_id()!=currentPlayer.get_id()).findFirst().get());
      }

      // Rule 2 check if last pit is empty pit && its current players pit
      if (currentPit.getPables() == 1 && currentPlayer.getPlayersPits().contains(currentPit.getPitIndex())) {
        int movePables = currentPit.peek() + game.getPit(currentPit.getOppositePit()).peek();
        game.getPit(currentPlayer.getMancalaPit()).add(movePables);
      }

      // Rule 3 check if any one side of pits are empty
      Boolean isPlayerAllPitsEmpty =
          Arrays.stream(game.getPlayers())
              .map(
                  player ->
                      player.getPlayersPits().stream()
                          .map(pit -> game.getPit(pit).isEmpty())
                          .allMatch(empty -> empty == true))
              .anyMatch(player -> player == true);
      if (isPlayerAllPitsEmpty) {
        game.setStatus(GameStatus.FINISHED);
        if (game.getPit(game.getPlayers()[0].getMancalaPit()).getPables()
            > game.getPit(game.getPlayers()[1].getMancalaPit()).getPables()) {
          game.setWinnerPlayer(game.getPlayers()[0]);
        } else {
          game.setWinnerPlayer(game.getPlayers()[1]);
        }
      }
      gameRepository.save(game);
      return game;
    } else {
      throw new GameNotFoundException();
    }
  }

  @Override
  public Game findById(String gameId) {
    return gameRepository.findById(gameId).get();
  }

  @Override
  public void delete(String gameId) {
    Optional<Game>gameOptional=gameRepository.findById(gameId);
    if(gameOptional.isPresent()){
      gameRepository.delete(gameOptional.get());
    }else{
      throw new GameNotFoundException();
    }

  }
}
