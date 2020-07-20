package com.sushant.mancala.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

/** Player dto for api calls. */
@Getter
@Builder
public class PlayerDto {
  @Schema(description = "Player's id", example = "player1")
  private String _id;
}
