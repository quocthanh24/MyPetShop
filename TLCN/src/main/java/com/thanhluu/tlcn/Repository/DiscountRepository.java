package com.thanhluu.tlcn.Repository;

import com.thanhluu.tlcn.Entity.DiscountEntity;
import com.thanhluu.tlcn.Enum.DiscountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface DiscountRepository extends JpaRepository<DiscountEntity, UUID> {
  Optional<DiscountEntity> findByDiscountCode(String discountCode);
  Optional<DiscountEntity> findByStatusIs(DiscountStatus discountStatus);
  Optional<DiscountEntity> findByIdAndStatusIs(UUID id, DiscountStatus status);
  Page<DiscountEntity> findAllByStatus(DiscountStatus status, Pageable pageable);
  Page<DiscountEntity> findAllByStatusIn(Collection<DiscountStatus> status, Pageable pageable);

}
