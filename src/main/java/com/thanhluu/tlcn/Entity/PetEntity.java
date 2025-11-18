package com.thanhluu.tlcn.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pets")
@Builder
public class PetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Column(nullable = false, unique = true)
    private String petCode;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "breed")
    private String breed;

    @Column(name = "age")
    private Integer age;

    @ManyToOne(fetch =  FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private UserEntity owner;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "pet")
    private MedicalRecordEntity medicalRecordEntity;

    @PrePersist
    public void createPetID() {

        if (this.petCode == null) {
            this.petCode = "PET" + String.format("%05d", (int)(Math.random() * 100000));
        }

    }
}
