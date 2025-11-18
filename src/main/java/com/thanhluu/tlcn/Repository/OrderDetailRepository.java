package com.thanhluu.tlcn.Repository;

import com.thanhluu.tlcn.Entity.OrderDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetailEntity, UUID> {
  @Query("SELECT CASE WHEN COUNT(od) > 0 THEN true ELSE false END " +
    "FROM OrderDetailEntity od " +
    "JOIN od.order o " +
    "JOIN o.customer u " +
    "WHERE u.id = :userId AND od.product.id = :productId " +
    "AND o.orderStatus = 'DELIVERED'") // Chỉ đếm đơn hàng hoàn thành, tránh pending
  boolean hasUserPurchasedProduct(@Param("userId") UUID userId, @Param("productId") UUID productId);
}
