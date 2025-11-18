package com.thanhluu.tlcn.DTO.response.MedicalRecordDetail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MRDResponse {
    private String healthCondition;
    private String medicalHistory;
    private LocalDateTime updatedDate;
    private Double temperature;
    private String vaccines;
    private String diagnosisResult;
}
