package com.sushant.mancala.service;

import com.sushant.mancala.dto.GameDto;

import java.util.List;

/** Game service interface */
public interface GameService {

  /**
   * Get all games for repository.
   *
   * @return List of games
   */
  List<GameDto> getGames();

  /**
   * Create and join new game. Game will be saved in the repository.
   *
   * @param playerId Player's id
   * @return return newly created game.
   */
  GameDto createAndJoinGame(String playerId);

  /**
   * Request to join the game.
   *
   * @param gameId game's id
   * @param playerId player's id
   * @return Game details
   */
  GameDto joinGame(String gameId, String playerId);

  /**
   * Request to make a move.
   *
   * @param gameId Game's id
   * @param playerId Player's id
   * @param pitId Pit's id
   * @return game details
   */
  GameDto makeMove(String gameId, String playerId, int pitId);

  /**
   * Find game by game id
   *
   * @param gameId game's id
   * @return Game's details
   */
  GameDto findById(String gameId);

  /**
   * Delete the game permanently
   *
   * @param gameId game id.
   */
  void delete(String gameId);
}
