package com.thanhluu.tlcn.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ratings")
public class RatingEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Min(value = 1, message = "Điểm đánh giá tối thiểu là 1")
  @Max(value = 5, message = "Điểm đánh giá tối đa là 5")
  private Integer ratingScore;
  @Column(length = 1000)
  private String comment;
  @Column(nullable = false)
  private LocalDateTime createdDate;
  private LocalDateTime updatedDate;

  @ManyToOne
  @JoinColumn(name = "product_id")
  private ProductEntity product;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserEntity customer;
}
