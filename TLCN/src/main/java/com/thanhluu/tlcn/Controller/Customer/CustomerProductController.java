package com.thanhluu.tlcn.Controller.Customer;

import com.thanhluu.tlcn.DTO.response.Product.ProductResp;
import com.thanhluu.tlcn.Service.Employee.IImageService;
import com.thanhluu.tlcn.Service.Employee.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;

@RestController
@RequestMapping("/api/customers/products")
public class CustomerProductController {
  @Autowired
  private IProductService productService;

  @GetMapping
  public ResponseEntity<Page<ProductResp>> getAllProducts(Pageable pageable) {
    return new ResponseEntity<>(productService.getAllProduct(pageable), HttpStatus.OK);
  }

  @GetMapping("/{id}/thumbnail")
  public ResponseEntity<?> getProductThumbnail(@PathVariable String id) {
    byte[] imageData = productService.getProductThumbnail(id);
    return ResponseEntity.ok()
      .header("Content-Type", "image/jpeg")
      .header("Content-Disposition", "inline")
      .body(imageData);
  }

  @GetMapping("/get-by-category")
  public ResponseEntity<Page<ProductResp>> getProductsByCategory(@RequestParam String categoryName,Pageable pageable) {
    return new ResponseEntity<>(productService.findByCategory(categoryName, pageable), HttpStatus.OK);
  }

  @GetMapping("/search")
  public ResponseEntity<Page<ProductResp>> findProductByKeyword(@RequestParam String keyword,Pageable pageable) {
    return new ResponseEntity<>(productService.findByKeyWord(keyword, pageable), HttpStatus.OK);
  }
}
