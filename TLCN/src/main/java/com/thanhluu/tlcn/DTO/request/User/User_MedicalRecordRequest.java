package com.thanhluu.tlcn.DTO.request.User;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User_MedicalRecordRequest {

    @NotEmpty(message = "Owner name should not be empty")
    private String fullName;

    @NotEmpty(message = "Owner's phone number should not be empty")
    private String phoneNumber;

    @NotEmpty(message = "Owner's address should not be empty")
    private String address;


}
