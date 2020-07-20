package com.sushant.mancala.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/** Player dto for api calls. */
@Data
@Builder
public class PlayerDto {
  @Schema(description = "Player's id", example = "player1")
  private String _id;
}
