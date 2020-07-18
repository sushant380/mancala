package com.sushant.mancala.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class Pit {
  private int pables;
  private int pitIndex;
  @EqualsAndHashCode.Exclude private Integer nextPit;
  @EqualsAndHashCode.Exclude private Integer oppositePit;
  private boolean mancala;

  public Pit(int pables, int pitIndex, boolean mancala) {
    this.pables = pables;
    this.pitIndex = pitIndex;
    this.mancala = mancala;
  }

  public void add(int pb) {
    this.pables = this.pables + pb;
  }

  public int put() {
    pables++;
    return pables;
  }

  public int peek() {
    int peekPables = pables;
    pables = 0;
    return peekPables;
  }

  public boolean isEmpty() {
    return pables == 0;
  }
}
