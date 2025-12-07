package com.thanhluu.tlcn.Repository;

import com.thanhluu.tlcn.Entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {

  Optional<CategoryEntity> findById(UUID id);
  Optional<CategoryEntity> findByName(String categoryName);
}
