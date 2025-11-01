package com.thanhluu.tlcn.Service.Employee;

import com.thanhluu.tlcn.DTO.request.Category.CategoryCreateRequest;
import com.thanhluu.tlcn.DTO.response.Category.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ICategoryService {

    CategoryResponse save(CategoryCreateRequest categoryRequestDTOCreate);

    Page<CategoryResponse> findAll(Pageable pageable);

    CategoryResponse update(CategoryCreateRequest categoryRequestDTOCreate, String id);
}
