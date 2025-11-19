package com.thanhluu.tlcn.Repository;

import com.thanhluu.tlcn.Entity.DiscountEntity;
import com.thanhluu.tlcn.Entity.UserDiscountEntity;
import com.thanhluu.tlcn.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserDiscountRepository extends JpaRepository<UserDiscountEntity, UUID> {
  Optional<UserDiscountEntity> findByCustomerAndDiscount(UserEntity customer, DiscountEntity discount);
}
