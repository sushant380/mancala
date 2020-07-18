package com.sushant.mancala.service;

import com.sushant.mancala.domain.Game;

import java.util.List;

public interface GameService {
    List<Game> getGames();
    Game createGame(String playerId);
    Game joinGame(String gameId,String playerId);
    Game makeMove(String gameId,String playerId, int pitId);
    Game findById(String gameId);
    void delete(String gameId);
}
