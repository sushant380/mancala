package com.sushant.mancala.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sushant.mancala.domain.Game;
import com.sushant.mancala.dto.GameDto;
import com.sushant.mancala.dto.PlayerDto;
import com.sushant.mancala.handler.GameHandler;
import com.sushant.mancala.service.GameService;
import com.sushant.mancala.utils.GameMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Base64Utils;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameController.class)
public class GameControllerTest {
  @MockBean GameService gameService;

  @Inject private MockMvc mockMvc;

  ObjectMapper mapper = new ObjectMapper();

  GameDto gameDto;

  @Value("${mancala.pits}")
  private int pits;

  @Value("${mancala.pables}")
  private int pables;

  GameHandler gameHandler;

  public static String PLAYER_1 = "TEST_PLAYER_1";

  public static String PLAYER_2 = "TEST_PLAYER_2";

  @BeforeEach
  void setup() {
    gameHandler=new GameHandler();
    Game game = gameHandler.getGame(pits, pables);
    game.set_id("Ad32fSr3fw454");
    gameDto = GameMapper.mapDto(game);

  }

  /**
   * Check happy scenario.
   *
   * @throws Exception
   */
  @Test
  public void createGame() throws Exception {

    when(gameService.createAndJoinGame(PLAYER_1)).thenReturn(gameDto);
    this.mockMvc
        .perform(
            post("/games")
                .header(
                    HttpHeaders.AUTHORIZATION,
                    "Basic " + Base64Utils.encodeToString((PLAYER_1+":test").getBytes()))
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(
            mvcResult -> {
              String json = mvcResult.getResponse().getContentAsString();
              JsonNode actualObject = mapper.readTree(json);
              assertThat(actualObject.findValue("_id").asText()).isEqualTo(gameDto.get_id());
            });
  }

  @Test
  void getGames() throws Exception {
    when(gameService.getGames()).thenReturn(Arrays.asList(gameDto));
    this.mockMvc
        .perform(
            get("/games")
                .header(
                    HttpHeaders.AUTHORIZATION,
                    "Basic " + Base64Utils.encodeToString((PLAYER_1+":test").getBytes()))
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(
            mvcResult -> {
              String json = mvcResult.getResponse().getContentAsString();
              List<GameDto> actualObject = mapper.readValue(json, List.class);
              assertThat(actualObject.size()).isEqualTo(1);
            });
  }

  @Test
  void getGamesById() throws Exception {
    when(gameService.findById(gameDto.get_id())).thenReturn(gameDto);
    this.mockMvc
        .perform(
            get("/games/"+gameDto.get_id())
                .header(
                    HttpHeaders.AUTHORIZATION,
                    "Basic " + Base64Utils.encodeToString((PLAYER_1+":test").getBytes()))
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(
            mvcResult -> {
              String json = mvcResult.getResponse().getContentAsString();
              JsonNode actualObject = mapper.readTree(json);
              assertThat(actualObject.findValue("_id").asText()).isEqualTo(gameDto.get_id());
            });
  }

  @Test
  void joinGame() throws Exception {
    gameDto.getPlayers()[0]=PlayerDto.builder()._id(PLAYER_1).build();
    gameDto.getPlayers()[1]=PlayerDto.builder()._id(PLAYER_2).build();
    when(gameService.joinGame(gameDto.get_id(), PLAYER_2))
        .thenReturn(gameDto);
    this.mockMvc
        .perform(
            put("/games/" + gameDto.get_id() + "/join")
                .header(
                    HttpHeaders.AUTHORIZATION,
                    "Basic " + Base64Utils.encodeToString((PLAYER_2+":test").getBytes()))
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(
            mvcResult -> {
              String json = mvcResult.getResponse().getContentAsString();
              JsonNode actualObject = mapper.readTree(json);
              assertThat(actualObject.findPath("players").get(1).findValue("_id").asText()).isEqualTo(PLAYER_2);
            });
  }

  @Test
  void makeMove() throws Exception {
      gameDto.getPits().get(0).setPables(0);
    when(gameService.makeMove(
            gameDto.get_id(),
            PLAYER_1,
            gameDto.getPits().get(0).getPitId()))
        .thenReturn(gameDto);
    this.mockMvc
        .perform(
            put("/games/" + gameDto.get_id() + "/pits/" + gameDto.getPits().get(0).getPitId())
                .header(
                    HttpHeaders.AUTHORIZATION,
                    "Basic " + Base64Utils.encodeToString((PLAYER_1+":test").getBytes()))
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(
            mvcResult -> {
              String json = mvcResult.getResponse().getContentAsString();
                JsonNode actualObject = mapper.readTree(json);
              assertThat(actualObject.findPath("pits").get(0).findValue("pables").asInt()).isEqualTo(0);
            });
  }

  @Test
  void deleteCall() throws Exception {
    doNothing().when(gameService).delete(gameDto.get_id());
    this.mockMvc
        .perform(
            delete("/games/" + gameDto.get_id())
                .header(
                    HttpHeaders.AUTHORIZATION,
                    "Basic " + Base64Utils.encodeToString("test:test".getBytes()))
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  void authenticationException() throws Exception {
    when(gameService.createAndJoinGame(PLAYER_1)).thenReturn(gameDto);
    this.mockMvc
        .perform(get("/games").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }
}
