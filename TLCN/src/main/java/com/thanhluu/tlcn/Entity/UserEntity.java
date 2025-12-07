package com.thanhluu.tlcn.Entity;


import com.thanhluu.tlcn.Enum.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
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

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "owner")
    private List<PetEntity> pets;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer", orphanRemoval = true)
    private List<OrderEntity> orders;

    @OneToMany(fetch = FetchType.LAZY,orphanRemoval = true, mappedBy = "customer")
    private List<UserDiscountEntity> discounts;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "employee")
    private List<MedicalRecordEntity> medicalRecordEntities;

    @OneToOne(mappedBy = "customer", orphanRemoval = true)
    private CartEntity cart;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AppointmentEntity>  appointmentsAsCustomer;

    @OneToMany(mappedBy = "employee")
    private List<AppointmentEntity> appointmentsAsEmployee;

    @OneToMany(mappedBy = "customer")
    private List<RatingEntity> ratings;
}
