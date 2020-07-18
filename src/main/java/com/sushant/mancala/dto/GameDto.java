package com.sushant.mancala.dto;

import com.sushant.mancala.common.GameStatus;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Builder
@Data
public class GameDto extends RepresentationModel<GameDto> {
  private String _id;
  private PlayerDto[] players = new PlayerDto[2];
  private PlayerDto nextPlayer;
  private GameStatus status;
  private PlayerDto winnerPlayer;
  private List<PitDto> pits;
}
