package com.sushant.mancala.dto;

import com.sushant.mancala.common.GameStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;
import java.util.List;

/** Game dto for api calls. */
@Builder
@Data
public class GameDto extends RepresentationModel<GameDto> {
  @Schema(description = "Game id", example = "asdasda343242d2342232")
  private String _id;

  @Schema(description = "Players in the game", example = "player1,player2")
  private PlayerDto[] players = new PlayerDto[2];

  @Schema(description = "Next player move", example = "player1")
  private PlayerDto nextPlayer;

  @Schema(description = "Current status of the game", example = "STARTED")
  private GameStatus status;

  private PlayerDto winnerPlayer;

  @Schema(description = "Pits in the game")
  private List<PitDto> pits;
}
