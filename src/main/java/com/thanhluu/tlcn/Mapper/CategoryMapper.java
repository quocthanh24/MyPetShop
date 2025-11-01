package com.thanhluu.tlcn.Mapper;

import com.thanhluu.tlcn.DTO.request.Category.CategoryCreateRequest;
import com.thanhluu.tlcn.DTO.response.Category.CategoryResponse;
import com.thanhluu.tlcn.Entity.CategoryEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryEntity toEntity(CategoryCreateRequest categoryRequestDTOCreate);

    CategoryResponse toResponseDTO(CategoryEntity categoryEntity);

    List<CategoryResponse> toListResponseDTO(List<CategoryEntity> entities);
}
