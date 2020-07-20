package com.sushant.mancala.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sushant.mancala.common.GameStatus;
import com.sushant.mancala.domain.Game;
import com.sushant.mancala.domain.Player;
import com.sushant.mancala.dto.GameDto;
import com.sushant.mancala.dto.PlayerDto;
import com.sushant.mancala.exception.GameNotFoundException;
import com.sushant.mancala.exception.InvalidGameException;
import com.sushant.mancala.handler.GameHandler;
import com.sushant.mancala.repository.GameRepository;
import com.sushant.mancala.service.impl.GameServiceImpl;
import com.sushant.mancala.utils.GameMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.sushant.mancala.constant.GameConstants.PLAYER_1;
import static com.sushant.mancala.constant.GameConstants.PLAYER_2;
import static com.sushant.mancala.constant.GameConstants.PLAYER_3;
import static com.sushant.mancala.constant.GameConstants.RANDOM_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
public class GameServiceTest {
  @InjectMocks GameService gameService = new GameServiceImpl();

  @Mock GameRepository gameRepository;

  ObjectMapper mapper = new ObjectMapper();

  GameDto gameDto;

  Game gameModel;
  @Mock GameHandler gameHandler;
  GameHandler anotherHandler;
  @Value("${mancala.pits}")
  private int pits;
  @Value("${mancala.pables}")
  private int pables;

  @BeforeEach
  void setup() {
    anotherHandler = new GameHandler();
    gameModel = anotherHandler.getGame(pits, pables);
    gameModel.set_id("Ad32fSr3fw454");
    gameDto = GameMapper.mapDto(gameModel);
    ReflectionTestUtils.setField(gameService, "pits", pits);
    ReflectionTestUtils.setField(gameService, "pables", pables);
  }

  @Test
  public void getGames() {
    when(gameRepository.findAll()).thenReturn(Arrays.asList(gameModel));
    List<GameDto> gameDtoList = gameService.getGames();
    assertThat(gameDtoList).isNotEmpty();
  }

  @Test
  public void createAndJoinGame() {
    when(gameRepository.save(any(Game.class))).thenReturn(gameModel);
    when(gameHandler.getGame(anyInt(), anyInt())).thenReturn(gameModel);
    doNothing().when(gameHandler).joinGame(gameModel, PLAYER_1);
    GameDto game = gameService.createAndJoinGame(PLAYER_1);
    assertThat(game).isNotNull();
    assertThat(game.get_id()).isEqualTo(gameModel.get_id());
  }

  @Test
  public void joinGame() {
    gameModel.getPlayers()[0] = new Player(PLAYER_1, 7);
    gameDto.getPlayers()[1] = PlayerDto.builder()._id(PLAYER_2).build();
    when(gameRepository.findById(gameModel.get_id())).thenReturn(Optional.of(gameModel));
    when(gameRepository.save(gameModel)).thenReturn(gameModel);
    doCallRealMethod().when(gameHandler).joinGame(any(Game.class), any(String.class));
    GameDto game = gameService.joinGame(gameModel.get_id(), PLAYER_2);
    assertThat(game.getPlayers()[1]).isNotNull();
    assertThat(game.getPlayers()[1].get_id()).isEqualTo(PLAYER_2);
  }

  @Test
  public void joinGameNotFound() {
    when(gameRepository.findById(gameModel.get_id())).thenReturn(Optional.empty());
    assertThrows(GameNotFoundException.class,()->gameService.joinGame(gameModel.get_id(), PLAYER_2));
  }

  @Test
  public void joinRunnigGame() {
    gameModel.setStatus(GameStatus.STARTED);
    when(gameRepository.findById(gameModel.get_id())).thenReturn(Optional.of(gameModel));
    assertThrows(
        InvalidGameException.class, () -> gameService.joinGame(gameModel.get_id(), PLAYER_3));
  }

  @Test
  public void makeMove() {
    anotherHandler.joinGame(gameModel, PLAYER_1);
    anotherHandler.joinGame(gameModel, PLAYER_2);
    when(gameRepository.findById(gameModel.get_id())).thenReturn(Optional.of(gameModel));
    doCallRealMethod().when(gameHandler).move(gameModel, PLAYER_1, 2);
    GameDto game = gameService.makeMove(gameModel.get_id(), PLAYER_1, 2);
    assertThat(game.getNextPlayer().get_id()).isEqualTo(PLAYER_2);
    assertThat(game.getPits().get(1).getPables()).isEqualTo(0);
  }

  @Test
  public void makeMoveGameNotFound() {
    when(gameRepository.findById(gameModel.get_id())).thenReturn(Optional.empty());
    assertThrows(GameNotFoundException.class,()->gameService.makeMove(gameModel.get_id(), PLAYER_2,1));
  }

  @Test
  public void findById() {
    when(gameRepository.findById(gameModel.get_id())).thenReturn(Optional.of(gameModel));
    GameDto game = gameService.findById(gameModel.get_id());
    assertThat(game).isNotNull();
    assertThat(game.get_id()).isEqualTo(gameModel.get_id());
  }

  @Test
  public void gameNotFound() {
    when(gameRepository.findById(any(String.class))).thenReturn(Optional.empty());
    assertThrows(GameNotFoundException.class, () -> gameService.findById(RANDOM_ID));
  }

  @Test
  public void delete() {
    when(gameRepository.findById(gameModel.get_id())).thenReturn(Optional.of(gameModel));
    doNothing().when(gameRepository).delete(gameModel);
    gameService.delete(gameModel.get_id());
  }

  @Test
  public void deleteGameNotFound() {
    when(gameRepository.findById(any(String.class))).thenReturn(Optional.empty());
    assertThrows(GameNotFoundException.class, () -> gameService.delete(RANDOM_ID));
  }
}
