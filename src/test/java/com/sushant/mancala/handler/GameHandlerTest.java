package com.sushant.mancala.handler;

import com.sushant.mancala.common.GameStatus;
import com.sushant.mancala.domain.Game;
import com.sushant.mancala.exception.InvalidPlayerMoveException;
import com.sushant.mancala.exception.UnauthorizedPlayerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.sushant.mancala.constant.GameConstants.PABLES;
import static com.sushant.mancala.constant.GameConstants.PITS;
import static com.sushant.mancala.constant.GameConstants.PLAYER_1;
import static com.sushant.mancala.constant.GameConstants.PLAYER_2;
import static com.sushant.mancala.constant.GameConstants.PLAYER_3;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GameHandlerTest {

  GameHandler gameHandler = new GameHandler();

  @BeforeEach
  void setup() {}

  @Test
  public void getGame() {
    Game game = gameHandler.getGame(PITS, PABLES);
    assertThat(game).isNotNull();
    assertThat(game.get_id()).isNull();
    assertThat(game.getPits().size()).isEqualTo(PITS * 2);
    assertThat(game.getStatus()).isEqualTo(GameStatus.CREATED);
  }

  @Test
  public void joinGame() {
    Game game = gameHandler.getGame(PITS, PABLES);
    gameHandler.joinGame(game, PLAYER_1);
    assertThat(game.getPlayers()[0].get_id()).isEqualTo(PLAYER_1);
    assertThat(game.getPlayers()[1]).isNull();
    assertThat(game.getStatus()).isEqualTo(GameStatus.CREATED);
    assertThat((game.getPlayers()[0].getPlayersPits())).isNotEmpty();
    assertThat(game.getPlayers()[0].getMancalaPit()).isEqualTo(PITS);
    assertThat(game.getNextPlayer().get_id()).isEqualTo(PLAYER_1);
    // Join with same id
    gameHandler.joinGame(game, PLAYER_1);
    assertThat(game.getPlayers()[1]).isNull();

    // join second player
    gameHandler.joinGame(game, PLAYER_2);
    assertThat(game.getPlayers()[1].get_id()).isEqualTo(PLAYER_2);
    assertThat(game.getStatus()).isEqualTo(GameStatus.STARTED);
    assertThat((game.getPlayers()[1].getPlayersPits())).isNotEmpty();
    assertThat(game.getPlayers()[1].getMancalaPit()).isEqualTo((PITS * 2));
    assertThat(game.getNextPlayer().get_id()).isEqualTo(PLAYER_1);
  }

  @Test
  public void move() {
    Game game = gameHandler.getGame(PITS, PABLES);
    gameHandler.joinGame(game, PLAYER_1);
    gameHandler.joinGame(game, PLAYER_2);

    assertThrows(UnauthorizedPlayerException.class, () -> gameHandler.move(game, PLAYER_3, 1));

    gameHandler.move(game, PLAYER_1, 1);
    assertThat(game.getPit(1).getPables()).isEqualTo(0);
    assertThat(game.getPit(2).getPables()).isEqualTo(5);
    assertThat(game.getNextPlayer().get_id()).isEqualTo(PLAYER_2);

    gameHandler.move(game, PLAYER_2, 11);
    assertThat(game.getNextPlayer().get_id()).isEqualTo(PLAYER_1);
    assertThat(game.getPit(game.getPlayers()[1].getMancalaPit()).getPables()).isEqualTo(1);

    assertThrows(InvalidPlayerMoveException.class, () -> gameHandler.move(game, PLAYER_2, 10));

    assertThrows(InvalidPlayerMoveException.class, () -> gameHandler.move(game, PLAYER_1, 11));

    gameHandler.move(game, PLAYER_1, 2);
    assertThat(game.getNextPlayer().get_id()).isEqualTo(PLAYER_1);
    gameHandler.move(game, PLAYER_1, 3);

    assertThrows(InvalidPlayerMoveException.class, () -> gameHandler.move(game, PLAYER_2, 16));
    assertThrows(InvalidPlayerMoveException.class, () -> gameHandler.move(game, PLAYER_2, 11));
    gameHandler.move(game, PLAYER_2, 13);
    gameHandler.move(game, PLAYER_1, 4);
    gameHandler.move(game, PLAYER_2, 12);
    gameHandler.move(game, PLAYER_1, 5);
    gameHandler.move(game, PLAYER_2, 9);

    int oppositeMancalaCount = game.getPit(14).getPables();
    int firstPitCount = game.getPits().get(1).getPables();
    gameHandler.move(game, PLAYER_1, 6);
    assertThat(game.getPit(14).getPables()).isEqualTo(oppositeMancalaCount);
    assertThat(game.getPit(1).getPables()).isNotEqualTo(firstPitCount);

    gameHandler.move(game, PLAYER_2, 9);
    gameHandler.move(game, PLAYER_1, 4);

    game.getPit(8).setPables(0);
    game.getPit(9).setPables(0);
    game.getPit(10).setPables(0);
    game.getPit(11).setPables(0);
    game.getPit(12).setPables(0);
    game.getPit(13).setPables(1);

    gameHandler.move(game, PLAYER_2, 13);
    assertThat(game.getWinnerPlayer()).isNotNull();
    assertThat(game.getStatus()).isEqualTo(GameStatus.FINISHED);
  }
}
