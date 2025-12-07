package com.thanhluu.tlcn.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "orderDetails")
public class OrderDetailEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;
  private Integer quantity;
  private Double price;
  private Double totalPrice;

  @ManyToOne
  @JoinColumn(name = "order_id")
  private OrderEntity order;

  @ManyToOne
  @JoinColumn(name = "product_id")
  private ProductEntity product;
}
