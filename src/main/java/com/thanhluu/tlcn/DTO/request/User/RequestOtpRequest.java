package com.thanhluu.tlcn.DTO.request.User;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestOtpRequest {

    @NotEmpty(message = "Email should not be empty")
    private String gmail;

    @NotEmpty(message = "Full name should not be empty")
    private String fullName;
}

