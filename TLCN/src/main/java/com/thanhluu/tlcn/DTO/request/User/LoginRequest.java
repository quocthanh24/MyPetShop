package com.thanhluu.tlcn.DTO.request.User;


import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginRequest {

    @NotEmpty(message = "Gmail should not be empty")
    private String gmail;

    @NotEmpty(message = "Password should not be empty")
    private String password;
}
