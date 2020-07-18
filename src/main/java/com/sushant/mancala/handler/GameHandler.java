package com.sushant.mancala.handler;

import com.sushant.mancala.common.GameStatus;
import com.sushant.mancala.domain.Game;
import com.sushant.mancala.domain.Pit;
import com.sushant.mancala.domain.Player;
import com.sushant.mancala.exception.GameFullException;
import com.sushant.mancala.exception.InvalidPlayerMoveException;
import com.sushant.mancala.exception.UnauthorizedPlayerException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class GameHandler {
  public static Game getGame(int pits, int pables) {
    Game game = new Game();
    game.setPits(getPits(pits, pables));
    return game;
  }

  private static List<Pit> getPits(int pitsCount, int pables) {
    int totalPits = (pitsCount * 2);
    List<Pit> pits =
        IntStream.range(0, totalPits)
            .mapToObj(
                index -> {
                  boolean mancalaPit = (index + 1) % pitsCount == 0 && index != 0;
                  return new Pit(mancalaPit ? 0 : pables, index + 1, mancalaPit);
                })
            .collect(Collectors.toList());

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

  public static boolean joinGame(Game game, String playerId) {
    Player[] players = game.getPlayers();
    if (players[0] == null) {
      players[0] = new Player(playerId, (game.getPits().size() / 2));
      players[0].setPlayersPits(
          IntStream.range(1, (game.getPits().size() / 2)).boxed().collect(Collectors.toList()));
      game.setNextPlayer(players[0]);
      return true;
    } else if (players[1] == null) {
      players[1] = new Player(playerId, game.getPits().size());
      players[1].setPlayersPits(
          IntStream.range((game.getPits().size() / 2) + 1, game.getPits().size())
              .boxed()
              .collect(Collectors.toList()));
      game.setStatus(GameStatus.STARTED);
      return true;
    } else {
      throw new GameFullException();
    }
  }

  private static Player getPlayerById(Game game, String playerId) {
    Player[] players = game.getPlayers();
    if (players[0] != null && players[0].get_id().equals(playerId)) {
      return players[0];
    } else if (players[1] != null && players[1].get_id().equals(playerId)) {
      return players[1];
    } else {
      throw new UnauthorizedPlayerException();
    }
  }

  public static void move(Game game, String playerId, int pitId) {
    Player currentPlayer = GameHandler.getPlayerById(game, playerId);
    if (game.getNextPlayer() != null && !game.getNextPlayer().equals(currentPlayer)) {
      throw new InvalidPlayerMoveException();
    }
    Pit currentPit = game.getPit(pitId);
    int pables = currentPit.peek();
    for (int i = 0; i < pables; i++) {
      Pit nextPit = game.getPit(currentPit.getNextPit());
      // do not put into opposite players mancala
      if (nextPit.isMancala() && !nextPit.equals(game.getPit(currentPlayer.getMancalaPit()))) {
        nextPit = game.getPit(nextPit.getNextPit());
      }
      nextPit.put();
      currentPit = nextPit;
    }
    // Rule 1 check if last pable is in current players mancala
    if (currentPit.equals(game.getPit(currentPlayer.getMancalaPit()))) {
      game.setNextPlayer(currentPlayer);
    } else {
      game.setNextPlayer(
          Arrays.stream(game.getPlayers())
              .filter(player -> player.get_id() != currentPlayer.get_id())
              .findFirst()
              .get());
    }

    // Rule 2 check if last pit is empty pit && its current players pit
    if (currentPit.getPables() == 1
        && currentPlayer.getPlayersPits().contains(currentPit.getPitIndex())) {
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
  }
}
