package com.thanhluu.tlcn.Service.Employee.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thanhluu.tlcn.DTO.request.Product.ProductRequest;
import com.thanhluu.tlcn.DTO.response.Message.MessageResponse;
import com.thanhluu.tlcn.DTO.response.Product.ProductResp;
import com.thanhluu.tlcn.Entity.CategoryEntity;
import com.thanhluu.tlcn.Entity.ImageItemEntity;
import com.thanhluu.tlcn.Entity.ProductEntity;
import com.thanhluu.tlcn.Enum.ErrorCode;
import com.thanhluu.tlcn.Exeception.BadRequestException;
import com.thanhluu.tlcn.Mapper.ProductMapper;
import com.thanhluu.tlcn.Repository.CategoryRepository;
import com.thanhluu.tlcn.Repository.ImageItemRepository;
import com.thanhluu.tlcn.Repository.ProductRepository;
import com.thanhluu.tlcn.Service.Employee.IImageService;
import com.thanhluu.tlcn.Service.Employee.IProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private IImageService imageService;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ImageItemRepository imageItemRepository;
    @Autowired
    private EntityManager entityManager;

    @Override
    public Page<ProductResp> getAllProduct(Pageable pageable) {
        return productRepository.findAll(pageable)
          .map(productMapper::toResponseDTO);
    }


    @Override
    public ProductResp findById(String id) {
        ProductEntity productEntity = productRepository.findById(UUID.fromString(id))
          .orElseThrow(() -> new BadRequestException(ErrorCode.CATEGORY_NOT_FOUND));
        return productMapper.toResponseDTO(productEntity);
    }

    @Override
    @Transactional
    public MessageResponse deleteById(String id) {
        UUID productId = UUID.fromString(id);
        log.info("Starting to delete product with id: {}", productId);

        ProductEntity product = productRepository.findByIdWithImages(productId)
            .orElseThrow(() -> {
                log.error("Product not found with id: {}", productId);
                return new BadRequestException(ErrorCode.PRODUCT_NOT_FOUND);
            });
        String thumbnailUrl = product.getThumbnailUrl();
        List<String> imageUrls = product.getImages() != null 
            ? product.getImages().stream()
                .map(ImageItemEntity::getImageUrl)
                .toList()
            : new ArrayList<>();

        log.info("Deleting product from database");
        // Xóa images trước bằng repository
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            imageItemRepository.deleteAll(product.getImages());
            entityManager.flush();
            log.info("Deleted {} images from database", imageUrls.size());
        }
        
        // Xóa product bằng deleteById để tránh vấn đề với detached entity
        productRepository.deleteById(productId);
        entityManager.flush(); // Đảm bảo xóa product được ghi vào DB
        log.info("Product deleted and flushed successfully");

        // Sau khi commit, xóa file vật lý
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                log.info("Transaction committed, deleting files from MinIO");
                deleteFromMinIO(thumbnailUrl, imageUrls);
            }
        });
        
        log.info("Product deletion completed successfully for id: {}", productId);
        return MessageResponse.builder()
            .message("Deleted product successfully")
            .status(200)
            .timestamp(LocalDateTime.now())
            .build();
    }

    // Method riêng để xóa MinIO, dễ retry/cleanup sau
    private void deleteFromMinIO(String thumbnailUrl, List<String> imageUrls) {
        if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
            try {
                imageService.deleteImage(thumbnailUrl);
                log.info("Deleted thumbnail from MinIO: {}", thumbnailUrl);
            } catch (Exception e) {
                log.error("Failed to delete thumbnail from MinIO: {}", e.getMessage());
                // Có thể lưu vào cleanup queue để retry sau
            }
        }

        imageUrls.forEach(url -> {
            try {
                imageService.deleteImage(url);
                log.info("Deleted image from MinIO: {}", url);
            } catch (Exception e) {
                log.error("Failed to delete image from MinIO: {}", e.getMessage());
            }
        });
    }

    @Override
    public Page<ProductResp> findByKeyWord(String keyword, Pageable pageable) {
        return productRepository.findByKeyWord(keyword,pageable)
          .map(productMapper::toResponseDTO);
    }

    @Override
    public ProductResp createWithImage(String productJson, MultipartFile thumbnail) {
        try {
            ProductRequest request = objectMapper.readValue(productJson, ProductRequest.class);
            if (thumbnail != null && !thumbnail.isEmpty()) {
                String imageUrl = imageService.uploadImage(thumbnail, "products");
                request.setThumbnailUrl(imageUrl);
            }
            CategoryEntity categoryEntity = categoryRepository.findByName(request.getCategoryName())
              .orElseThrow(() -> new BadRequestException(ErrorCode.CATEGORY_NOT_FOUND));
            ProductEntity productEntity = productMapper.toEntity(request);
            productEntity.setCategory(categoryEntity);
            productRepository.save(productEntity);
            return productMapper.toResponseDTO(productEntity);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }
    }

    @Override
    public Page<ProductResp> findByCategory(String categoryName, Pageable pageable) {
        return productRepository.findByCategory(categoryName,pageable)
          .map(productMapper::toResponseDTO);
    }

    /**
     * Create product with multiple images
     * Parses JSON string to ProductRequest and handles image uploads
     * Separates thumbnail image from regular images
     */
    @Override
    public ProductResp createWithImages(String productJson, MultipartFile thumbnail, List<MultipartFile> files) {
        try {
            ProductRequest request = objectMapper.readValue(productJson, ProductRequest.class);
            ProductEntity productEntity = productMapper.toEntity(request);
            
            // Validate and set category
            CategoryEntity categoryEntity = categoryRepository.findByName(request.getCategoryName())
                .orElseThrow(() -> new BadRequestException(ErrorCode.CATEGORY_NOT_FOUND));
            productEntity.setCategory(categoryEntity);
            
            // Save product first to get the ID
            productEntity = productRepository.save(productEntity);
            
            // Upload and set thumbnail image (if provided)
            if (thumbnail != null && !thumbnail.isEmpty()) {
                try {
                    String thumbnailUrl = imageService.uploadImage(thumbnail, "products");
                    productEntity.setThumbnailUrl(thumbnailUrl);
                } catch (Exception e) {
                    throw new IllegalArgumentException(e.getMessage());
                }
            }
            
            // Upload and save regular images (excluding thumbnail)
            List<ImageItemEntity> imageItems = productEntity.getImages();

            if (files != null && !files.isEmpty()) {
                for (MultipartFile file : files) {
                    if (file != null && !file.isEmpty()) {
                        try {
                            String imageUrl = imageService.uploadImage(file, "products");
                            ImageItemEntity imageItem = new ImageItemEntity();
                            imageItem.setImageUrl(imageUrl);
                            imageItem.setProduct(productEntity);
                            imageItems.add(imageItem);
                        } catch (Exception e) {
                            throw new IllegalArgumentException(e.getMessage());
                        }
                    }
                }
            }

            // Set images and save product
            if (!imageItems.isEmpty()) {
                productEntity.setImages(imageItems);
            }
            productEntity = productRepository.save(productEntity);
            return productMapper.toResponseDTO(productEntity);
        } catch (BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Failed to create product with images: {}", ex.getMessage(), ex);
            throw new BadRequestException(ErrorCode.INVALID_REQUEST_BODY);
        }
    }

    /**
     * Update product with multiple images
     * Parses JSON string to ProductRequest, updates existing product and handles image uploads
     */
    @Override
    @Transactional
    public ProductResp updateWithImages(String id, String productJson, MultipartFile thumbnail, List<MultipartFile> files) {
        try {
            ProductEntity existingProduct = productRepository.findByIdWithImages(UUID.fromString(id))
                .orElseThrow(() -> new BadRequestException(ErrorCode.PRODUCT_NOT_FOUND));

            ProductRequest request = objectMapper.readValue(productJson, ProductRequest.class);

            // MapStruct sẽ chỉ update field nào có giá trị khác null
            productMapper.updateProductFromRequest(request, existingProduct);

            // Lưu lại danh sách URL ảnh cũ để xóa file vật lý sau
            List<String> oldImageUrls = existingProduct.getImages() != null 
                ? existingProduct.getImages().stream()
                    .map(ImageItemEntity::getImageUrl)
                    .filter(url -> url != null && !url.isEmpty())
                    .toList()
                : new ArrayList<>();

            String oldThumbnailUrl = existingProduct.getThumbnailUrl();

            // Xử lý thumbnail
            if (thumbnail != null && !thumbnail.isEmpty()) {
                try {
                    imageItemRepository.deleteByImageUrl(oldThumbnailUrl);
                    String thumbnailUrl = imageService.uploadImage(thumbnail, "products");
                    existingProduct.setThumbnailUrl(thumbnailUrl);
                } catch (Exception e) {
                    log.error("Failed to upload thumbnail: {}", e.getMessage());
                    throw new BadRequestException(ErrorCode.INVALID_REQUEST_BODY);
                }
            }
            // Xử lý images (files)
            if (files != null && !files.isEmpty()) {
                // Clear existing images - orphanRemoval sẽ tự động xóa
                if (existingProduct.getImages() != null) {
                    existingProduct.getImages().clear();
                }

                // Upload and save new images
                List<ImageItemEntity> imageItems = existingProduct.getImages();
                for (MultipartFile file : files) {
                    if (file != null && !file.isEmpty()) {
                        try {
                            String imageUrl = imageService.uploadImage(file, "products");
                            ImageItemEntity imageItem = new ImageItemEntity();
                            imageItem.setImageUrl(imageUrl);
                            imageItem.setProduct(existingProduct);
                            imageItems.add(imageItem);
                        } catch (Exception e) {
                            log.error("Failed to upload image: {}", e.getMessage());
                            throw new BadRequestException(ErrorCode.INVALID_REQUEST_BODY);
                        }
                    }
                }

                if (!imageItems.isEmpty()) {
                    existingProduct.setImages(imageItems);
                }
            }

            // Save updated product - orphanRemoval sẽ tự động xóa ảnh cũ khỏi DB
            existingProduct = productRepository.save(existingProduct);
            entityManager.flush();

            // Sau khi commit, xóa file vật lý cũ
            final String finalOldThumbnailUrl = oldThumbnailUrl;
            final List<String> finalOldImageUrls = oldImageUrls;
            
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    deleteFromMinIO(finalOldThumbnailUrl, finalOldImageUrls);
                }
            });

            return productMapper.toResponseDTO(existingProduct);
        } catch (BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Failed to update product with images: {}", ex.getMessage(), ex);
            throw new BadRequestException(ErrorCode.INVALID_REQUEST_BODY);
        }
    }

    @Override
    public byte[] getProductThumbnail(String id) {
        ProductEntity product = productRepository.findById(UUID.fromString(id))
          .orElseThrow(() -> new BadRequestException(ErrorCode.PRODUCT_NOT_FOUND));

        String thumbnailUrl = product.getThumbnailUrl();
        if (thumbnailUrl == null || thumbnailUrl.isEmpty()) {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST_BODY);
        }

        return imageService.downloadImage(thumbnailUrl);
    }

}
