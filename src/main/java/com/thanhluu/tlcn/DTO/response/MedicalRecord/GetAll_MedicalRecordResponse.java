package com.thanhluu.tlcn.DTO.response.MedicalRecord;

import com.thanhluu.tlcn.DTO.response.Pet.Pet_MedicalRecordResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAll_MedicalRecordResponse {

    private UUID id;

    private String createdBy;

    private Date createdDate;

    private Pet_MedicalRecordResponse pet;


}
