package com.thanhluu.tlcn.Repository;

import com.thanhluu.tlcn.Entity.CartEntity;
import com.thanhluu.tlcn.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, UUID> {
  Optional<CartEntity> findByCustomer(UserEntity customer);
}
