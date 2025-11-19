package com.thanhluu.tlcn.Controller.Employee;

import com.thanhluu.tlcn.Enum.OrderStatus;
import com.thanhluu.tlcn.Service.Customer.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees/delivery/orders")
public class DeliveryController {

  @Autowired
  private IOrderService orderService;

  @GetMapping
  public ResponseEntity<?> getAllOrdersArePaid(Pageable pageable) {
    return new ResponseEntity<>(orderService.getAllOrdersArePaid(OrderStatus.PAID, pageable), HttpStatus.OK);
  }

  @PutMapping("/delevering/{orderNumber}")
  public ResponseEntity<?> updateOrderStatusToDelivering(@PathVariable String orderNumber) {
    return new ResponseEntity<>(orderService.updateOrderStatus(orderNumber, OrderStatus.DELEVERING), HttpStatus.OK);
  }

  @PutMapping("/delevered/{orderNumber}")
  public ResponseEntity<?> updateOrderStatusToDelivered(@PathVariable String orderNumber) {
    return new ResponseEntity<>(orderService.updateOrderStatus(orderNumber, OrderStatus.DELIVERED), HttpStatus.OK);
  }
}
