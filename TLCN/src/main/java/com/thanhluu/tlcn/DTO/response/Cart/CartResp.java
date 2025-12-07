package com.thanhluu.tlcn.DTO.response.Cart;

import com.thanhluu.tlcn.DTO.response.Product.Product_CartResp;
import com.thanhluu.tlcn.Entity.ProductEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartResp {
  private UUID cartId;
  private Double totalPrice;
  private List<Product_CartResp> products;
}
