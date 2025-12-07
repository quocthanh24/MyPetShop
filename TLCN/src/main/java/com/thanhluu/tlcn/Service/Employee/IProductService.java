package com.thanhluu.tlcn.Service.Employee;

import com.thanhluu.tlcn.DTO.response.Message.MessageResponse;
import com.thanhluu.tlcn.DTO.response.Product.ProductResp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IProductService {

    Page<ProductResp> getAllProduct(Pageable pageable);
    ProductResp findById(String id);
    Page<ProductResp> findByCategory(String categoryName, Pageable pageable);
    Page<ProductResp> findByKeyWord(String keyword, Pageable pageable);
    ProductResp createWithImage(String productJson, MultipartFile thumbnail);
    MessageResponse deleteById(String id);
    ProductResp createWithImages(String productJson, MultipartFile thumbnail, List<MultipartFile> files);
    ProductResp updateWithImages(String id, String productJson, MultipartFile thumbnail, List<MultipartFile> files);
    byte[] getProductThumbnail(String id);
}
