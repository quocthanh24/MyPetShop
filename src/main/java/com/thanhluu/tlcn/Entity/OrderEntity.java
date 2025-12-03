package com.thanhluu.tlcn.Entity;

import com.thanhluu.tlcn.Enum.OrderStatus;
import com.thanhluu.tlcn.Enum.PaymentMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "orders")
public class OrderEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, unique = true)
  private String orderNumber;
  private String shippingAddress;
  private String phoneNumber;
  private String note;
  private Double totalPrice;
  private Double shippingFee;
  @Enumerated(EnumType.STRING)
  private PaymentMethod paymentMethod;
  private LocalDateTime orderDate;
  @Enumerated(EnumType.STRING)
  private OrderStatus orderStatus;

  @OneToOne
  @JoinColumn(name = "discount_id")
  private DiscountEntity discount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private UserEntity customer;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "order",orphanRemoval = true)
  private List<OrderDetailEntity> orderDetails;
}
