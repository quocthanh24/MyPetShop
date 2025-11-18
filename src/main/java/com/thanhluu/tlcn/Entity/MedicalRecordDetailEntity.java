package com.thanhluu.tlcn.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mrdetails")
public class MedicalRecordDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Column(name = "health_condition")
    private String healthCondition;

    @Column(name = "medical_history")
    private String medicalHistory;

    @Column(nullable = false)
    private Date updatedDate;

    @Column(nullable = false)
    private double temperature;

    @Column(name = "vaccines")
    private String vaccines;

    @Column(name = "diagnosis_result", nullable = false)
    private String diagnosisResult;


    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "medicalrecord_id", nullable = false)
    private MedicalRecordEntity medicalRecord;

}
