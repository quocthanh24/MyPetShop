package com.thanhluu.tlcn.Mapper;

import com.thanhluu.tlcn.DTO.request.Product.ProductRequest;
import com.thanhluu.tlcn.DTO.response.Product.ProductResp;
import com.thanhluu.tlcn.Entity.ImageItemEntity;
import com.thanhluu.tlcn.Entity.ProductEntity;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductEntity toEntity(ProductRequest req);

    @Mapping(source = "productEntity.category.name", target = "categoryName")
    @Mapping(target = "imageUrls", ignore = true)
    ProductResp toResponseDTO(ProductEntity productEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProductFromRequest(ProductRequest request, @MappingTarget ProductEntity product);

    @AfterMapping
    default void mapImageUrls(@MappingTarget ProductResp response, ProductEntity productEntity) {
        if (productEntity != null && productEntity.getImages() != null && !productEntity.getImages().isEmpty()) {
            List<String> imageUrls = productEntity.getImages().stream()
                    .map(ImageItemEntity::getImageUrl)
                    .collect(Collectors.toList());
            response.setImageUrls(imageUrls);
        }
    }
}
