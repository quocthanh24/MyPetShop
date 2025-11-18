package com.thanhluu.tlcn.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "medicalrecords")
public class MedicalRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch =  FetchType.EAGER)
    @JoinColumn(name = "employee_id", nullable = false)
    private UserEntity employee;

    @Column(nullable = false)
    private Date createdDate;

    @OneToOne
    @JoinColumn(name = "pet_id",  nullable = false, unique = true)
    private PetEntity pet;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "medicalRecord")
    private List<MedicalRecordDetailEntity> medicalRecordDetailEntities;
}
