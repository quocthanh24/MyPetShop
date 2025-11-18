package com.thanhluu.tlcn.Repository;


import com.thanhluu.tlcn.Entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
  Optional<OrderEntity> findByOrderNumber(String orderNumber);
}


