package com.thanhluu.tlcn.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "discount_users")
public class UserDiscountEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserEntity customer;

  @ManyToOne
  @JoinColumn(name = "discount_id")
  private DiscountEntity discount;

  private LocalDateTime usedAt;
}
