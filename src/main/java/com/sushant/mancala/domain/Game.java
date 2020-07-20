package com.sushant.mancala.domain;

import com.sushant.mancala.common.GameStatus;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/** Game model for storage. */
@Data
@Document("games")
public class Game {
  private static int DEFAULT_PABLES = 4;
  private static int DEFAULT_PITS = 7;
  private String _id;
  private List<Pit> pits;
  private Player[] players = new Player[2];
  private Player nextPlayer;
  private GameStatus status;
  private Player winnerPlayer;

  public Game() {
    this.status = GameStatus.CREATED;
  }

  public Pit getPit(int pitIndex) {
    return pitIndex > 0 ? pits.get(pitIndex - 1) : null;
  }
}
