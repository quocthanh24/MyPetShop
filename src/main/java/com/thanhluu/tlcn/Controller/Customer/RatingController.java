package com.thanhluu.tlcn.Controller.Customer;

import com.thanhluu.tlcn.DTO.request.Rating.RatingReq;
import com.thanhluu.tlcn.DTO.response.Rating.RatingResp;
import com.thanhluu.tlcn.Service.Customer.IRatingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
public class RatingController {
  @Autowired
  private IRatingService ratingService;

  @PostMapping("/rate-product/{productId}")
  public ResponseEntity<RatingResp> rateProduct(
    @PathVariable UUID productId,
    @RequestBody @Valid RatingReq req) {
    return new ResponseEntity<>(ratingService.rate(productId, req), HttpStatus.CREATED);
  }
}
