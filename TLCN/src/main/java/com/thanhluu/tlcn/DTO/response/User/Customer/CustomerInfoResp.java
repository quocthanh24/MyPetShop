package com.thanhluu.tlcn.DTO.response.User.Customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerInfoResp {
  private String fullName;
  private String gmail;
  private String phoneNumber;
}
