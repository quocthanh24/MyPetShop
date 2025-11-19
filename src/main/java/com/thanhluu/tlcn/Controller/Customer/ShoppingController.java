package com.thanhluu.tlcn.Controller.Customer;

import com.thanhluu.tlcn.DTO.request.Cart.AddToCartRequest;
import com.thanhluu.tlcn.DTO.response.Product.ViewProductFromCartResp;
import com.thanhluu.tlcn.Service.Customer.ICustomerService;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequestMapping("/api/customers")
public class ShoppingController {

  @Autowired
  private ICustomerService customerService;


  @GetMapping("/get-products-from-cart")
  public ResponseEntity<?> getAllProductFromCart(
    @RequestParam @NotNull(message = "User ID should not be null") String userId,
    Pageable pageable) {
    return new ResponseEntity<>(customerService.getProductsFromCart(userId,pageable), HttpStatus.OK);
  }

  @PostMapping("/add-to-cart")
  public ResponseEntity<?> addProductToCart(@RequestBody @Validated AddToCartRequest req) {
    return ResponseEntity.status(HttpStatus.CREATED).body(customerService.addToCart(req));
  }

}
