package com.thanhluu.tlcn.Repository;

import com.thanhluu.tlcn.Entity.ImageItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ImageItemRepository extends JpaRepository<ImageItemEntity, UUID> {
    List<ImageItemEntity> findByProductId(UUID productId);
    void deleteByProductId(UUID productId);
    void deleteByImageUrl(String imageUrl);
}

