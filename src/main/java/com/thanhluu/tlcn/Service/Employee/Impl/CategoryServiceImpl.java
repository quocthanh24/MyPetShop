package com.thanhluu.tlcn.Service.Employee.Impl;

import com.thanhluu.tlcn.DTO.request.Category.CreateCategoryRequest;
import com.thanhluu.tlcn.DTO.response.Category.CategoryResponse;
import com.thanhluu.tlcn.Entity.CategoryEntity;
import com.thanhluu.tlcn.Enum.ErrorCode;
import com.thanhluu.tlcn.Exeception.BadRequestException;
import com.thanhluu.tlcn.Mapper.CategoryMapper;
import com.thanhluu.tlcn.Repository.CategoryRepository;
import com.thanhluu.tlcn.Service.Employee.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public CategoryResponse save(CreateCategoryRequest categoryRequestDTOCreate) {

        CategoryEntity categoryEntity = categoryMapper.toEntity(categoryRequestDTOCreate);
        categoryRepository.save(categoryEntity);
        return categoryMapper.toResponseDTO(categoryEntity);
    }

    @Override
    public Page<CategoryResponse> findAll(Pageable pageable) {

        return categoryRepository.findAll(pageable)
                                 .map(categoryMapper::toResponseDTO);
    }

    @Override
    public CategoryResponse update(CreateCategoryRequest categoryRequestDTOCreate, String id) {

        CategoryEntity categoryEntity = categoryRepository.findById(UUID.fromString(id))
          .orElseThrow(() -> new BadRequestException(ErrorCode.CATEGORY_NOT_FOUND));
        if (categoryEntity.getName() != null) {
            categoryEntity.setName(categoryRequestDTOCreate.getName());
        }
        if (categoryEntity.getDescription() != null) {
            categoryEntity.setDescription(categoryRequestDTOCreate.getDescription());
        }
        categoryRepository.save(categoryEntity);
        return categoryMapper.toResponseDTO(categoryEntity);
    }

}
