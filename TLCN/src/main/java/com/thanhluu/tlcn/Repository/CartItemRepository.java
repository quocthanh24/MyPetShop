package com.thanhluu.tlcn.Repository;

import com.thanhluu.tlcn.Entity.CartEntity;
import com.thanhluu.tlcn.Entity.CartItemEntity;
import com.thanhluu.tlcn.Entity.ProductEntity;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartItemRepository extends JpaRepository<CartItemEntity, UUID> {
    Optional<CartItemEntity> findByCartAndProduct(CartEntity cart, ProductEntity product);
    Page<CartItemEntity> findByCart(CartEntity cart, Pageable pageable);
    @NotNull
    List<CartItemEntity> findAllById(@NotNull Iterable<UUID> ids);
}
