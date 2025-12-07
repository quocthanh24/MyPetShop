package com.thanhluu.tlcn.Entity;

import com.thanhluu.tlcn.Enum.AppointmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "appointments")
public class AppointmentEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;
  @Column(nullable = false)
  private LocalDateTime appointmentTime;
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private AppointmentStatus status;
  @Column(nullable = false)
  private LocalDateTime createdDate;
  private LocalDateTime modifiedDate;
  private String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "employee_id", nullable = false)
  private UserEntity employee;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_id", nullable = false)
  private UserEntity customer;
}
