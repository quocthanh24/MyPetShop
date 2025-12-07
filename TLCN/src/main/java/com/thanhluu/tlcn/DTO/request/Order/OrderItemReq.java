package com.thanhluu.tlcn.DTO.request.Order;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OrderItemReq {
  private String name;  // JSON: "name"
  private Integer quantity;  // JSON: "quantity"

  @Max(value = 200)
  private Integer length;    // JSON: "length"

  @Max(value = 200)
  private Integer width;     // JSON: "width"

  @Max(value = 200)
  private Integer height;    // JSON: "height"

  @Max(value = 1600000)
  private Integer weight;    // JSON: "weight"
}
