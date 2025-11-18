package com.thanhluu.tlcn.Controller.Employee.Discount;

import com.thanhluu.tlcn.DTO.request.Discount.CreateDiscountReq;
import com.thanhluu.tlcn.DTO.response.Discount.CreateDiscountResp;
import com.thanhluu.tlcn.DTO.response.Discount.SetDiscountStatusResp;
import com.thanhluu.tlcn.DTO.response.Discount.ViewDiscountResp;
import com.thanhluu.tlcn.Enum.DiscountStatus;
import com.thanhluu.tlcn.Service.Employee.IDiscountService;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees/discounts")
public class GenerateCode {
  @Autowired
  private IDiscountService discountService;

  @GetMapping("/status")
  public ResponseEntity<Page<ViewDiscountResp>> getDiscountCodes(
    Pageable pageable,
    @RequestParam
    DiscountStatus status) {
      return ResponseEntity.ok(discountService.getDiscountCodes(pageable, status));
  }

  @PostMapping("/create-manually")
  public ResponseEntity<CreateDiscountResp> createDiscountManually(
    @RequestBody @Validated CreateDiscountReq req) {
      return new ResponseEntity<>(discountService.createManually(req),  HttpStatus.CREATED);
  }

  @PostMapping("/create-automatically")
  public ResponseEntity<CreateDiscountResp> createAutomatically(
    @RequestBody @Validated CreateDiscountReq req) {
    return new ResponseEntity<>(discountService.createAutomatically(req),  HttpStatus.CREATED);
  }

  @PatchMapping("/set-status/{id}")
  public ResponseEntity<SetDiscountStatusResp> setStatusToInactive(@PathVariable String id) {
      return new ResponseEntity<>(discountService.setDiscountToInactive(id), HttpStatus.OK);
  }

  @PatchMapping("/set-status")
  public ResponseEntity<Page<SetDiscountStatusResp>> setStatusToExpired(
    Pageable pageable) {
    return new ResponseEntity<>(discountService.setDiscountToExpired(pageable), HttpStatus.OK);
  }
}
