package com.sushant.mancala.domain;

import com.sushant.mancala.exception.GameFullException;
import com.sushant.mancala.exception.UnauthorizedPlayerException;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
@Document("games")
public class Game {
  private String _id;
  private List<Pit> pits;
  private static int DEFAULT_PABLES = 4;
  private static int DEFAULT_PITS = 7;
  private Player[] players = new Player[2];
  private Player nextPlayer;
  private GameStatus status;
  private Player winnerPlayer;

  public Game() {
    this(DEFAULT_PITS,DEFAULT_PABLES);
  }
  public Game(int pitsCount, int pables) {
    int totalPits = (pitsCount * 2);
    pits =
            IntStream.range(0, totalPits)
                    .mapToObj(index ->{
                      boolean mancalaPit=(index+1) % pitsCount == 0 && index!=0;
                      return  new Pit(mancalaPit?0:pables, index+1,mancalaPit );
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
    this.status = GameStatus.CREATED;
  }

  public Pit getPit(int pitIndex) {
    return pits.get(pitIndex-1);
  }

  public boolean joinGame(String playerId) {
    if (this.players[0] == null) {
      this.players[0] = new Player(playerId, (this.getPits().size() / 2));
      this.players[0].setPlayersPits(
          IntStream.range(1, (this.getPits().size() / 2))
              .boxed()
              .collect(Collectors.toList()));
      this.nextPlayer=this.players[0];
      return true;
    } else if (this.players[1] == null) {
      this.players[1] = new Player(playerId, this.getPits().size());
      this.players[1].setPlayersPits(
          IntStream.range((this.getPits().size() / 2) + 1, this.getPits().size())
              .boxed()
              .collect(Collectors.toList()));
      this.status = GameStatus.STARTED;
      return true;
    } else {
      throw new GameFullException();
    }
  }

  public Player getPlayerById(String playerId) {
    if (players[0] != null && players[0].get_id().equals(playerId)) {
      return players[0];
    } else if (players[1] != null && players[1].get_id().equals(playerId)) {
      return players[1];
    } else {
      throw new UnauthorizedPlayerException();
    }
  }
}
