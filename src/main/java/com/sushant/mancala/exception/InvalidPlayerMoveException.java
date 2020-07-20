package com.sushant.mancala.exception;

public class InvalidPlayerMoveException extends RuntimeException {
    public InvalidPlayerMoveException() {
        super();
    }

    public InvalidPlayerMoveException(final String message) {
        super(message);
    }
}
