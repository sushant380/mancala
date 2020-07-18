package com.sushant.mancala.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
@Builder
public class PitDto extends RepresentationModel<PitDto> {
  private int pables;
  private int pitId;
  private boolean mancala;
}
