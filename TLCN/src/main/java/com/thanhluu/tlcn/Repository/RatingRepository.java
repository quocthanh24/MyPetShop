package com.thanhluu.tlcn.Repository;

import com.thanhluu.tlcn.Entity.RatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RatingRepository extends JpaRepository<RatingEntity, UUID> {
  boolean existsByCustomerIdAndProductId(UUID customerId, UUID productId);
}
