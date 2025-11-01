package com.thanhluu.tlcn.Entity;


import com.thanhluu.tlcn.Enum.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Data // sinh getter, setter, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "gmail", unique = true, nullable = false)
    private String gmail;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "full_name",  nullable = false)
    private String fullName;

    @Column(name = "gender")
    private String gender;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "dob", nullable = false)
    private Date dob;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;


    // mappedBy -> trỏ đến prop của table được ánh xạ
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "owner")
    private List<PetEntity> pets;


    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "employee")
    private List<MedicalRecordEntity> medicalRecordEntities;

}
