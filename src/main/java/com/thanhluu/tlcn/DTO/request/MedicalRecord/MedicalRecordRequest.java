package com.thanhluu.tlcn.DTO.request.MedicalRecord;

import com.thanhluu.tlcn.DTO.request.MedicalRecordDetail.MedicalRecordDetailRequest;
import com.thanhluu.tlcn.DTO.request.Pet.Pet_MedicalRecordRequest;
import com.thanhluu.tlcn.DTO.request.User.User_MedicalRecordRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

import static java.time.LocalDateTime.now;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicalRecordRequest {

    private LocalDateTime createdDate = now();

    private String employeePhoneNumber;

    private Pet_MedicalRecordRequest pet;

    private User_MedicalRecordRequest owner;

    private MedicalRecordDetailRequest recordDetails;
}
