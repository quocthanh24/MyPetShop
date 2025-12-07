package com.thanhluu.tlcn.DTO.response.MedicalRecordDetail;

import com.thanhluu.tlcn.DTO.response.Pet.Pet_MedicalRecordResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewAllDetailByPhoneNumberResp {
  private UUID medicalRecordId;
  private String createdBy;
  private LocalDateTime createdDate;
  private Pet_MedicalRecordResponse pet;
  private List<MRDResponse> medicalRecordDetails;
}
