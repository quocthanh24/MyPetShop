package com.thanhluu.tlcn.Mapper;

import com.thanhluu.tlcn.DTO.request.Product.ProductRequest;
import com.thanhluu.tlcn.DTO.response.Product.ProductResponse;
import com.thanhluu.tlcn.Entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductEntity toEntity(ProductRequest req);

    @Mapping(source = "productEntity.category.name" , target = "categoryName")
    ProductResponse toResponseDTO(ProductEntity productEntity);
}
