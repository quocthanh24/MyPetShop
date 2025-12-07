package com.thanhluu.tlcn.DTO.request.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestOtpRequest {
    @NotEmpty(message = "Email should not be empty")
    @Email(message = "Invalid email format")
    private String gmail;
}

