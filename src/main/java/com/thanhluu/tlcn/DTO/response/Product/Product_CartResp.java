package com.thanhluu.tlcn.DTO.response.Product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product_CartResp {
  private UUID productId;
  private String productName;
  private Integer quantity;

}
