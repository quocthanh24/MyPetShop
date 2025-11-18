package com.thanhluu.tlcn.DTO.request.User;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyOtpRegisterRequest {

    @NotEmpty(message = "Gmail should not be empty")
    private String gmail;

    @NotEmpty(message = "Password should not be empty")
    private String password;

    @NotEmpty(message = "Name should not be empty")
    private String fullName;

    @NotEmpty(message = "Gender should not be empty")
    @Pattern(regexp = "(?i)^(Nam|Nữ)$", // (?i) = ignore case, ^ $ = exact match
      message = "Gender should contain only Male or Female")
    private String gender;

    @NotEmpty(message = "Phone number should not be empty")
    private String phoneNumber;

    @NotEmpty(message = "Address should not be empty")
    private String address;

    @NotNull(message = "Date of birth should not be empty")
    private Date dob;

    @NotEmpty(message = "OTP code should not be empty")
    private String otpCode;
}

