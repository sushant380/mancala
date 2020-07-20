package com.sushant.mancala.handler;

import com.sushant.mancala.common.GameStatus;
import com.sushant.mancala.domain.Game;
import com.sushant.mancala.domain.Pit;
import com.sushant.mancala.domain.Player;
import com.sushant.mancala.exception.InvalidPlayerMoveException;
import com.sushant.mancala.exception.UnauthorizedPlayerException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Handler for all operations in the game. This class is responsible for 1. Creating a new game 2.
 * Registering players 3. Assigning pits to players 4. Moving the pables in pits. 5. Restricting
 * access to unauthorized players
 */
@Component
public class GameHandler {
  /**
   * Create new game with given pits and pables values. Game will always have double number of pits
   * and it will assign default pables to each pit. This will also decide which pit is mancala pit.
   *
   * @param pits Number of pits per player.
   * @param pables Number of pables in each pit.
   * @return Game with pits
   */
  public Game getGame(int pits, int pables) {
    Game game = new Game();
    game.setPits(getPits(pits, pables));
    return game;
  }

  /**
   * Create pits for the game
   *
   * @param pitsCount Number of pits for each player
   * @param pables Number of pables in each pits
   * @return list of pits.
   */
  private List<Pit> getPits(int pitsCount, int pables) {
    int totalPits = (pitsCount * 2);
    List<Pit> pits =
        IntStream.range(0, totalPits)
            .mapToObj(
                index -> {
                  // middle and last pit is mancala pit
                  boolean mancalaPit = (index + 1) % pitsCount == 0 && index != 0;
                  return new Pit(mancalaPit ? 0 : pables, index + 1, mancalaPit);
                })
            .collect(Collectors.toList());
    // find next and opposite pit of each pit. if pit is mancala then it will not have opposite pit.
    pits.forEach(
        pit -> {
          int pitIndex = pit.getPitIndex();
          int nextPitIndex = pitIndex == totalPits ? 1 : pitIndex + 1;
          int oppositePitIndex = totalPits - pitIndex;
          pit.setNextPit(nextPitIndex);
          if (!pit.isMancala()) {
            pit.setOppositePit(oppositePitIndex);
          }
        });
    return pits;
  }

  /**
   * Join the game. Only two players are allowed for each game. if all players are joined then
   * further entry will not be allowed. Once all players joined game will set to STARTED.
   *
   * @param game Game to which player wants to join
   * @param playerId Player's registered id.
   */
  public void joinGame(Game game, String playerId) {
    Player[] players = game.getPlayers();
    if (players[0] == null) {
      players[0] = new Player(playerId, (game.getPits().size() / 2));
      // assign half of the pits to player 1
      players[0].setPlayersPits(
          IntStream.range(1, (game.getPits().size() / 2)).boxed().collect(Collectors.toList()));
      game.setNextPlayer(players[0]);
    } else if (players[0].get_id() == playerId) {
      // if already registered player is sending a request to join again
      return;
    } else if (players[1] == null && players[0].get_id() != playerId) {
      players[1] = new Player(playerId, game.getPits().size());
      // assign half of the pits to player 2
      players[1].setPlayersPits(
          IntStream.range((game.getPits().size() / 2) + 1, game.getPits().size())
              .boxed()
              .collect(Collectors.toList()));
      game.setStatus(GameStatus.STARTED);
    } else if (players[1].get_id() == playerId) {
      return;
    }
  }

  /**
   * Get player based on player's id in the game
   *
   * @param game Game for which player is registered
   * @param playerId Player's id
   * @return Player
   */
  private Player getPlayerById(Game game, String playerId) {
    Player[] players = game.getPlayers();
    if (players[0] != null && players[0].get_id().equals(playerId)) {
      return players[0];
    } else if (players[1] != null && players[1].get_id().equals(playerId)) {
      return players[1];
    } else {
      throw new UnauthorizedPlayerException();
    }
  }

  /**
   * Make a move in the game. if its not player's move then throw exception.
   *
   * @param game Game
   * @param playerId Player's id who is making a move
   * @param pitId Pit id for move.
   */
  public void move(Game game, String playerId, int pitId) {
    Player currentPlayer = getPlayerById(game, playerId);

    // If Its not player's turn
    if ((game.getNextPlayer() != null && !game.getNextPlayer().equals(currentPlayer))) {
      throw new InvalidPlayerMoveException("You can not make a move. Its other player's turn");
    }
    // If Player is sending invalid pit id.
    if (pitId > currentPlayer.getPlayersPits().size()) {
      throw new InvalidPlayerMoveException("You can not make a move. Its invalid pit");
    }

    // If Player is sending pit id that is not his/her pit.
    if (!currentPlayer.getPlayersPits().contains(pitId)) {
      throw new InvalidPlayerMoveException("You can not make a move. Its not your pit");
    }

    Pit currentPit = game.getPit(pitId);

    // If Player is making a move for empty pit.
    if (currentPit.getPables() == 0) {
      throw new InvalidPlayerMoveException("You can not make a move. The pit is empty");
    }

    int pables = currentPit.peek();
    for (int i = 0; i < pables; i++) {
      Pit nextPit = game.getPit(currentPit.getNextPit());
      // do not put into opposite player's mancala
      if (nextPit.isMancala() && !nextPit.equals(game.getPit(currentPlayer.getMancalaPit()))) {
        nextPit = game.getPit(nextPit.getNextPit());
      }
      nextPit.put();
      currentPit = nextPit;
    }
    // Rule 1 check if last pable is in current player's mancala. if yes then player get another
    // turn
    if (currentPit.equals(game.getPit(currentPlayer.getMancalaPit()))) {
      game.setNextPlayer(currentPlayer);
    } else {
      game.setNextPlayer(
          Arrays.stream(game.getPlayers())
              .filter(player -> player.get_id() != currentPlayer.get_id())
              .findFirst()
              .get());
    }

    // Rule 2 check if last pit is empty pit && its current players pit then move all pables into
    // player's mancala.
    if (currentPit.getPables() == 1
        && currentPlayer.getPlayersPits().contains(currentPit.getPitIndex())) {
      int movePables = currentPit.peek() + game.getPit(currentPit.getOppositePit()).peek();
      game.getPit(currentPlayer.getMancalaPit()).add(movePables);
    }

    // Rule 3 check if any one side of pits are empty. If any one side of the game is empty then
    // game is over. find winner by counting pables in mancala.
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
  }
}
