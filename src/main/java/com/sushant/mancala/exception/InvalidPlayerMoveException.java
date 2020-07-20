package com.sushant.mancala.exception;

/**
 * Throw exception if player is making a wrong move. it could be player not having his turn or
 * player moving for empty pit or player is not using his pit for move
 */
public class InvalidPlayerMoveException extends RuntimeException {
  public InvalidPlayerMoveException() {
    super();
  }

  public InvalidPlayerMoveException(final String message) {
    super(message);
  }
}
