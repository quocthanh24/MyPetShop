package com.thanhluu.tlcn.DTO.response.MedicalRecord;

import com.thanhluu.tlcn.DTO.response.MedicalRecordDetail.MRDResponse;
import com.thanhluu.tlcn.DTO.response.Pet.Pet_MedicalRecordResponse;
import com.thanhluu.tlcn.DTO.response.User.User_MedicalRecordResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicalRecordResponse {

    private UUID medicalRecordId;
    private Date createdDate;
    private String employeePhoneNumber;
    private Pet_MedicalRecordResponse pet;
    private User_MedicalRecordResponse owner;
    private MRDResponse recordDetails;
}
