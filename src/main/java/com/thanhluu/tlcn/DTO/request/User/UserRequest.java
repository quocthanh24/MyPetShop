package com.thanhluu.tlcn.DTO.request.User;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    @NotEmpty(message = "Gmail should not be empty")
    private String gmail;

    @NotEmpty(message = "Password should not be empty")
    private String password;

    @NotEmpty(message = "Name should not be empty")
    private String fullName;

    @NotNull
    private String gender;

    @NotEmpty(message = "Phone number should not be empty")
    private String phoneNumber;

    @NotEmpty(message = "Address should not be empty")
    private String address;

    @NotNull(message = "Date of birth should not be empty")
    private Date dob;

}
