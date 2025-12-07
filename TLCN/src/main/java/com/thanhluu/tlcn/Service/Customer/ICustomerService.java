package com.thanhluu.tlcn.Service.Customer;

import com.thanhluu.tlcn.DTO.request.Cart.AddToCartRequest;
import com.thanhluu.tlcn.DTO.request.Product.ProductRequest;
import com.thanhluu.tlcn.DTO.response.Cart.CartResp;
import com.thanhluu.tlcn.DTO.response.Product.Product_CartResp;
import com.thanhluu.tlcn.DTO.response.Product.ViewProductFromCartResp;
import com.thanhluu.tlcn.Entity.CartEntity;
import com.thanhluu.tlcn.Entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ICustomerService {
  CartResp addToCart(AddToCartRequest req);
  CartResp updateCart(AddToCartRequest req);
  CartEntity getOrCreateCart(UserEntity customer);
  CartResp deleteItemFromCart(String userId, String itemId);
  Page<ViewProductFromCartResp> getProductsFromCart(String userId,Pageable pageable);
  CartResp removeProductFromCart(String userId, List<UUID> cartItemIds);
}
