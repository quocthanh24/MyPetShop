package com.thanhluu.tlcn.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "carts")
public class CartEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private Double totalPrice;

  @OneToOne
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private UserEntity customer;

  @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<CartItemEntity> items = new ArrayList<>();
}
