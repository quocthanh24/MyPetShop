package com.thanhluu.tlcn.Repository;
import com.thanhluu.tlcn.Entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
  Optional<ProductEntity> findById(UUID id);

  @Query("SELECT p FROM ProductEntity p LEFT JOIN FETCH p.images WHERE p.id = :id")
  Optional<ProductEntity> findByIdWithImages(UUID id);

  @Query("SELECT p FROM ProductEntity p " +
    "JOIN p.category c " +
    "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
    "   OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
  Page<ProductEntity> findByKeyWord(String keyword, Pageable pageable);

  @Query("SELECT p FROM ProductEntity p " +
    "JOIN p.category c " +
    "WHERE LOWER(c.name) LIKE LOWER(:categoryName)")
  Page<ProductEntity> findByCategory(String categoryName, Pageable pageable);

  @Modifying(clearAutomatically = true)
  @Transactional
  @Query(value = """
    UPDATE products p
    SET
        average_rating = CASE
            WHEN (SELECT COUNT(*) FROM ratings r WHERE r.product_id = :productId) = 0
            THEN CAST(:newScore AS DOUBLE PRECISION)  -- Sửa: Dùng CAST thay ::
            ELSE
                ((p.average_rating * p.rating_count + CAST(:newScore AS DOUBLE PRECISION)) / (p.rating_count + 1))  -- Tương tự
        END,
        rating_count = p.rating_count + 1
    WHERE p.id = :productId
    """,
    nativeQuery = true)
  int updateAverageRating(@Param("productId") UUID productId, @Param("newScore") Integer newScore);
}
