package com.thanhluu.tlcn.DTO.request.MedicalRecordDetail;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicalRecordDetailUpdateRequest {

    @NotEmpty(message = "Health Condition should not be empty")
    private String healthCondition;

    @NotNull(message = "Medicall History should not be null")
    private String medicalHistory;

    @NotEmpty(message = "Updated Date should not be empty")
    private Date updatedDate;

    @NotEmpty(message = "Temperature should not be empty")
    private double temperature;

    @NotNull(message = "Vaccines should not be null")
    private String vaccines;

    @NotBlank(message = "Diagnosis Result should not be blank")
    private String diagnosisResult;
}
