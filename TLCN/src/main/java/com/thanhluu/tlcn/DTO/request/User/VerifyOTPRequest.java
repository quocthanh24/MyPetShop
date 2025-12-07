package com.thanhluu.tlcn.DTO.request.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyOTPRequest {
  @NotBlank
  @Email
  private String gmail;
  @NotBlank
  private String otp;
}
