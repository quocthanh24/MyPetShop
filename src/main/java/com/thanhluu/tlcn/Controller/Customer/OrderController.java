package com.thanhluu.tlcn.Controller.Customer;


import com.thanhluu.tlcn.DTO.request.Order.CartOrderReq;
import com.thanhluu.tlcn.DTO.request.Order.OrderCreateReq;
import com.thanhluu.tlcn.DTO.response.Order.CancelOrderResp;
import com.thanhluu.tlcn.DTO.response.Order.OrderResp;
import com.thanhluu.tlcn.Service.Customer.IOrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/customers/orders")
public class OrderController {
  @Autowired
  private IOrderService orderService;

  @PostMapping
  public ResponseEntity<OrderResp> placeOrder(
    @RequestParam @NotEmpty(message = "User Id should not be null") String userId,
    @RequestBody @Valid CartOrderReq req) {
    OrderResp created = orderService.cartOrder(userId, req);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @PostMapping("/buy-now")
  public ResponseEntity<OrderResp> buyNow(@RequestBody OrderCreateReq request) {
    OrderResp created = orderService.buyNowOrder(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @PostMapping("/cancel")
  public ResponseEntity<CancelOrderResp> cancelOrder(@RequestParam String orderNumber) {
    return ResponseEntity.ok(orderService.cancelOrder(orderNumber));
  }

}


