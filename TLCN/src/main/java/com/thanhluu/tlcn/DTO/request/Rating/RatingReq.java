package com.thanhluu.tlcn.DTO.request.Rating;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class RatingReq {
  @NotNull(message = "customer ID should not be null")
  private UUID customerId;
  @Min(value = 1,message = "Min rating score is 1")
  @Max(value = 5, message = "Max rating score is 5")
  private Integer ratingScore;
  private String comment;
}
