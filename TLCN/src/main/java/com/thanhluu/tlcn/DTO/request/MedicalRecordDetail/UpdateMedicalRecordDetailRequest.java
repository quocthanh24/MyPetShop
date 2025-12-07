package com.thanhluu.tlcn.DTO.request.MedicalRecordDetail;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMedicalRecordDetailRequest {

    @NotEmpty(message = "Health Condition should not be empty")
    private String healthCondition;

    @NotNull(message = "Medicall History should not be null")
    private String medicalHistory;

    @NotEmpty(message = "Updated Date should not be empty")
    private Date updatedDate;

    @DecimalMin(value = "30.0", message = "Temperature must be at least 30°C")
    private Double temperature;

    @NotNull(message = "Vaccines should not be null")
    private String vaccines;

    @NotBlank(message = "Diagnosis Result should not be blank")
    private String diagnosisResult;
}
