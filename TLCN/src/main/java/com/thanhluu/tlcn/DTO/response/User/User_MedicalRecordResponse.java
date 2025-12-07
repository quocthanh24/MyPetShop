package com.thanhluu.tlcn.DTO.response.User;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User_MedicalRecordResponse {

    private String fullName;

    private String phoneNumber;

    private String address;
}
