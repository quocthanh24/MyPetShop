package com.thanhluu.tlcn.Repository;
import com.thanhluu.tlcn.Entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
  Optional<ProductEntity> findById(UUID id);

  @Query("SELECT p FROM ProductEntity p " +
    "JOIN p.category c " +
    "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
    "   OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
  Page<ProductEntity> findByKeyWord(String keyword, Pageable pageable);

  @Query("SELECT p FROM ProductEntity p " +
    "JOIN p.category c " +
    "WHERE LOWER(c.name) LIKE LOWER(:categoryName)")
  Page<ProductEntity> findByCategory(String categoryName, Pageable pageable);
}
