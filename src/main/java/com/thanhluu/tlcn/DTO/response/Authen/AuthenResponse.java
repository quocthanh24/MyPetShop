package com.thanhluu.tlcn.DTO.response.Authen;

import com.thanhluu.tlcn.Enum.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenResponse {

    private String userId;

    private String accessToken;

    private String refreshToken;

    private Role role;
}
