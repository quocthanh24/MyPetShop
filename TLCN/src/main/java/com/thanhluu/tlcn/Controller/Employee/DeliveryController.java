package com.thanhluu.tlcn.Controller.Employee;

import com.thanhluu.tlcn.DTO.request.Shipment.ShipmentRequestFromOwner;
import com.thanhluu.tlcn.Enum.OrderStatus;
import com.thanhluu.tlcn.Service.Customer.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees/delivery/orders")
public class DeliveryController {

  @Autowired
  private IOrderService orderService;

  @GetMapping
  public ResponseEntity<?> getAllOrdersArePaidAndPending(Pageable pageable) {
    return new ResponseEntity<>(orderService.getAllOrdersArePaidAndPending(List.of(OrderStatus.PAID, OrderStatus.PENDING), pageable), HttpStatus.OK);
  }

  @PutMapping("/delevering/{orderNumber}")
  public ResponseEntity<?> updateOrderStatusToDelivering(@PathVariable String orderNumber) {
    return new ResponseEntity<>(orderService.updateOrderStatus(orderNumber, OrderStatus.OUT_FOR_DELIVERY), HttpStatus.OK);
  }

  @PutMapping("/delevered/{orderNumber}")
  public ResponseEntity<?> updateOrderStatusToDelivered(@PathVariable String orderNumber) {
    return new ResponseEntity<>(orderService.updateOrderStatus(orderNumber, OrderStatus.DELIVERED), HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<?> createOrder(@RequestBody ShipmentRequestFromOwner request) {
    return new ResponseEntity<>(orderService.createShippingOrderByOrderNumber(request), HttpStatus.OK);
  }
  
}
