package com.thanhluu.tlcn.DTO.response.Authen;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenResponse {

    private String accessToken;

    private String refreshToken;
}
