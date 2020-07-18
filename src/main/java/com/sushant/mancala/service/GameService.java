package com.sushant.mancala.service;

import com.sushant.mancala.dto.GameDto;
import java.util.List;

public interface GameService {
  List<GameDto> getGames();

  GameDto createAndJoinGame(String playerId);

  GameDto joinGame(String gameId, String playerId);

  GameDto makeMove(String gameId, String playerId, int pitId);

  GameDto findById(String gameId);

  void delete(String gameId);
}
