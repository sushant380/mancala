package com.sushant.mancala.domain;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class Player {
  private String _id;
  @EqualsAndHashCode.Exclude private Integer mancalaPit;
  @EqualsAndHashCode.Exclude private List<Integer> playersPits;

  public Player(String playerId, Integer mancalaPit) {
    this._id = playerId;
    this.mancalaPit = mancalaPit;
  }
}
