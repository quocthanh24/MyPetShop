package com.thanhluu.tlcn.Entity;

import com.thanhluu.tlcn.Enum.DiscountStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "discounts")
public class DiscountEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, unique = true)
  private String discountCode;
  @Column(nullable = false)
  private String discountName;
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DiscountStatus status;
  private Double percent;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private Integer totalLimit;
  private Integer usedCount = 0;

}
