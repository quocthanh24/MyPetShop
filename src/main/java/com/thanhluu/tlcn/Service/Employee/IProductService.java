package com.thanhluu.tlcn.Service.Employee;

import com.thanhluu.tlcn.DTO.request.Product.ProductRequest;
import com.thanhluu.tlcn.DTO.response.Message.MessageResponse;
import com.thanhluu.tlcn.DTO.response.Product.ProductResponse;
import com.thanhluu.tlcn.Entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IProductService {

    Page<ProductResponse> getAllProduct(Pageable pageable);
    ProductResponse findById(String id);
    Page<ProductResponse> findByCategory(String categoryName, Pageable pageable);
    Page<ProductResponse> findByKeyWord(String keyword, Pageable pageable);
    ProductResponse create(ProductRequest req);
    MessageResponse deleteById(String id);

}
