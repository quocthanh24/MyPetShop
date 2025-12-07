package com.thanhluu.tlcn.DTO.request.MedicalRecordDetail;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicalRecordDetailRequest {

    @NotEmpty
    private String healthCondition;

    @NotEmpty
    private String medicalHistory;

    @NotNull
    private LocalDateTime updatedDate;

    @NotEmpty
    private Double temperature;

    @NotNull
    private String vaccines;

    @NotEmpty
    private String diagnosisResult;

}
