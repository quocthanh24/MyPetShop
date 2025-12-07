package com.thanhluu.tlcn.DTO.request.User;

import lombok.Data;

@Data
public class ResetPasswordRequest {
  private String gmail;
  private String resetPassword;
  private String confirmPassword;
}
