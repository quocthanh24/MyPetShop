package com.thanhluu.tlcn.Controller.Customer;

import com.thanhluu.tlcn.DTO.request.Payment.CreateMomoPaymentRequest;
import com.thanhluu.tlcn.Service.Payment.IPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
@Slf4j
public class PaymentController {

  private final IPaymentService momoPaymentService;

  @PostMapping("/momo/create")
  public ResponseEntity<?> create(@Valid @RequestBody CreateMomoPaymentRequest request) {
    Map<String, Object> response = momoPaymentService.createPayment(
      request.getOrderId(),
      request.getAmount()
    );
    return ResponseEntity.ok(response);
  }

  @PostMapping("/momo/ipn")
  public ResponseEntity<String> ipn(@RequestBody Map<String, Object> payload) {
    return ResponseEntity.ok(momoPaymentService.momoIpn(payload));
  }


}
