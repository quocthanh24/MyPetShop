package com.thanhluu.tlcn.DTO.response.Product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewProductFromCartResp {
  private String cartItemId;
  private String productName;
  private Integer quantity;
  private String thumbnailUrl;
  private Double price;
}
