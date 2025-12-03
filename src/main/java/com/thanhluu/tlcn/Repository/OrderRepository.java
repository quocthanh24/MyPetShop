package com.thanhluu.tlcn.Repository;


import com.thanhluu.tlcn.Entity.OrderEntity;
import com.thanhluu.tlcn.Entity.UserEntity;
import com.thanhluu.tlcn.Enum.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
  Optional<OrderEntity> findByOrderNumber(String orderNumber);
  Page<OrderEntity> findAllByOrderStatusIn(Collection<OrderStatus> orderStatuses, Pageable pageable);

  Page<OrderEntity> findAllByCustomerAndOrderStatusIsNot(UserEntity customer, OrderStatus orderStatus, Pageable pageable);
}


