package com.thanhluu.tlcn.Service.Employee.Impl;

import com.thanhluu.tlcn.DTO.request.Product.ProductRequest;
import com.thanhluu.tlcn.DTO.response.Message.MessageResponse;
import com.thanhluu.tlcn.DTO.response.Product.ProductResponse;
import com.thanhluu.tlcn.Entity.CategoryEntity;
import com.thanhluu.tlcn.Entity.ProductEntity;
import com.thanhluu.tlcn.Enum.ErrorCode;
import com.thanhluu.tlcn.Exeception.BadRequestException;
import com.thanhluu.tlcn.Mapper.ProductMapper;
import com.thanhluu.tlcn.Repository.CategoryRepository;
import com.thanhluu.tlcn.Repository.ProductRepository;
import com.thanhluu.tlcn.Service.Employee.IImageService;
import com.thanhluu.tlcn.Service.Employee.IProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    @Override
    public Page<ProductResponse> getAllProduct(Pageable pageable) {
        return productRepository.findAll(pageable)
          .map(productMapper::toResponseDTO);
    }

    @Override
    public ProductResponse findById(String id) {
        ProductEntity productEntity = productRepository.findById(UUID.fromString(id))
          .orElseThrow(() -> new BadRequestException(ErrorCode.CATEGORY_NOT_FOUND));
        return productMapper.toResponseDTO(productEntity);
    }


    @Override
    public ProductResponse create(ProductRequest req) {
        ProductEntity productEntity = productMapper.toEntity(req);
        CategoryEntity categoryEntity = categoryRepository.findByName(req.getCategoryName())
          .orElseThrow(() -> new BadRequestException(ErrorCode.CATEGORY_NOT_FOUND));
        productEntity.setCategory(categoryEntity);
        productRepository.save(productEntity);
        return productMapper.toResponseDTO(productEntity);
    }

    @Override
    public MessageResponse deleteById(String id) {
        ProductEntity productEntity = productRepository.findById(UUID.fromString(id))
            .orElseThrow(() -> new BadRequestException(ErrorCode.PRODUCT_NOT_FOUND));
        String imageURL = productEntity.getImageURL();
        imageService.deleteImage(imageURL);
        productRepository.deleteById(UUID.fromString(id));
        return MessageResponse.builder()
          .message("Deleted product with id " + id + " successfully")
          .status(200)
          .timestamp(LocalDateTime.now())
          .build();
    }

    @Override
    public Page<ProductResponse> findByKeyWord(String keyword, Pageable pageable) {
        return productRepository.findByKeyWord(keyword,pageable)
          .map(productMapper::toResponseDTO);
    }

    @Override
    public Page<ProductResponse> findByCategory(String categoryName, Pageable pageable) {
        return productRepository.findByCategory(categoryName,pageable)
          .map(productMapper::toResponseDTO);
    }


}
