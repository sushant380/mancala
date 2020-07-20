package com.sushant.mancala.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

/** Pit dto for api calls */
@Data
@Builder
public class PitDto extends RepresentationModel<PitDto> {
  @Schema(description = "Number of pables in the pit", example = "4")
  private int pables;

  @Schema(description = "Pit id", example = "1")
  private int pitId;

  @Schema(description = "True if this pit is mancala pit", example = "true")
  private boolean mancala;
}
