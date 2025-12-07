package com.thanhluu.tlcn.DTO.response.Rating;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingResp {
  private Integer ratingScore;
  private String comment;
  private Double averageScore;
  private Integer ratingCount;
}
