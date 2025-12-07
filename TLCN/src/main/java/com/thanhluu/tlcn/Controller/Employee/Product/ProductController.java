package com.thanhluu.tlcn.Controller.Employee.Product;

import com.thanhluu.tlcn.Annotation.ValidImage;
import com.thanhluu.tlcn.DTO.response.Product.ProductResp;
import com.thanhluu.tlcn.Service.Employee.IImageService;
import com.thanhluu.tlcn.Service.Employee.IProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLConnection;
import java.util.List;

@RestController
@RequestMapping("/api/employees/products")
@Validated
@Slf4j
public class ProductController {
    @Autowired
    private IProductService productService;

    @Autowired
    private IImageService imageService;

    // ---------- Product CRUD ----------

    @GetMapping
    public ResponseEntity<?> getAllProducts(@PageableDefault(size = 10, page = 0) Pageable pageable) {
			return ResponseEntity.ok(productService.getAllProduct(pageable));
    }

		// Find product by keyword
		@GetMapping("/search")
		public ResponseEntity<?> findProductByKeyword(@RequestParam String keyword,Pageable pageable) {
			return ResponseEntity.ok(productService.findByKeyWord(keyword, pageable));
		}

		// Find product by category name
		@GetMapping("/get-by-category")
		public ResponseEntity<?> searchBy(@RequestParam String categoryName,Pageable pageable) {
			return ResponseEntity.ok(productService.findByCategory(categoryName, pageable));
		}

		// Find product by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") String id) {
			return ResponseEntity.ok(productService.findById(id));
    }

		@DeleteMapping("/{id}")
		public ResponseEntity<?> deleteProductById(@PathVariable String id) {
			return ResponseEntity.ok(productService.deleteById(id));
		}

    /**
     * Update product with multiple images (multipart/form-data)
     * Expects "data" as JSON string and "files" as list of images
     * Follows RESTful convention: PUT /{id}
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(
            @PathVariable("id") String id,
            @RequestPart("data") String productJson,
            @RequestPart(value = "thumbnail", required = false) @ValidImage  MultipartFile thumbnail,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        ProductResp product = productService.updateWithImages(id, productJson, thumbnail,files);
        return ResponseEntity.ok(product);
    }

    /**
     * Create product with image (multipart/form-data)
     * Expects "data" as JSON string and optional "file" for image
     */
    @PostMapping(value = "/create-with-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProductWithImage(
      @RequestPart("data") String productJson,
      @RequestPart(value = "thumbnail", required = false)
      @ValidImage MultipartFile thumbnail) {
      return new ResponseEntity<>(productService.createWithImage(productJson, thumbnail), HttpStatus.CREATED);
    }

    /**
     * Create product with multiple images (multipart/form-data)
     * Expects "data" as JSON string, optional "thumbnail" for thumbnail image, and "files" as list of regular images
     * JSON parsing and business logic handled in service layer
     */
    @PostMapping(value = "/create-with-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProductWithImages(
            @RequestPart("data") String productJson,
            @RequestPart(value = "thumbnail", required = false) @ValidImage MultipartFile thumbnail,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        ProductResp product = productService.createWithImages(productJson, thumbnail, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    // ---------- Product Image Endpoints ----------

    /**
     * Upload image for a specific product
     * Returns the image URL for manual update
     * 
     * Usage with custom validation:
     * - Max size: 5MB (default) - configure via @ValidImage(maxSize = ...)
     * - Allowed types: image/jpeg, image/png (default) - configure via @ValidImage(allowedTypes = {...})
     */
    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadProductImage(
            @RequestPart("file") @ValidImage MultipartFile file
    ) {
        try {
            String imageUrl = imageService.uploadImage(file, "products");
            return ResponseEntity.ok(imageUrl);
        } catch (Exception ex) {
            return new ResponseEntity<>("Failed to upload image: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * View product thumbnail image by Product ID (RESTful style)
     * Gets image from MinIO using product's thumbnailUrl stored in database
     */
    @GetMapping("/{id}/thumbnail")
    public ResponseEntity<?> viewProductThumbnailByProductId(@PathVariable("id") String id) {
        try {
            ProductResp product = productService.findById(id);
            if (product == null || product.getThumbnailUrl() == null || product.getThumbnailUrl().isEmpty()) {
                return new ResponseEntity<>("Product thumbnail not found", HttpStatus.NOT_FOUND);
            }
            
            InputStream stream = imageService.getImage(product.getThumbnailUrl());
            InputStreamResource resource = new InputStreamResource(stream);
            MediaType mediaType = resolveMediaType(product.getThumbnailUrl());
            
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"product-thumbnail-" + id + "\"")
                    .body(resource);
        } catch (Exception ex) {
            return new ResponseEntity<>("Failed to load thumbnail: " + ex.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * @deprecated Use /{id}/thumbnail instead
     * View product image by Product ID (RESTful style) - kept for backward compatibility
     */
    @Deprecated
    @GetMapping("/{id}/image")
    public ResponseEntity<?> viewProductImageByProductId(@PathVariable("id") String id) {
        return viewProductThumbnailByProductId(id);
    }

    /**
     * View product image by direct URL from MinIO (fallback endpoint)
     */
    @GetMapping("/image")
    public ResponseEntity<?> viewProductImageByUrl(@RequestParam("url") String imageUrl) {
        try {
            InputStream stream = imageService.getImage(imageUrl);
            InputStreamResource resource = new InputStreamResource(stream);
            MediaType mediaType = resolveMediaType(imageUrl);
            
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                    .body(resource);
        } catch (Exception ex) {
            return new ResponseEntity<>("Failed to load image: " + ex.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Delete product thumbnail by Product ID (RESTful style)
     */
    @DeleteMapping("/{id}/thumbnail")
    public ResponseEntity<?> deleteProductThumbnailByProductId(@PathVariable("id") String id) {
        try {
            ProductResp product = productService.findById(id);
            if (product == null || product.getThumbnailUrl() == null || product.getThumbnailUrl().isEmpty()) {
                return new ResponseEntity<>("Product thumbnail not found", HttpStatus.NOT_FOUND);
            }
            
            boolean deleted = imageService.deleteImage(product.getThumbnailUrl());
            if (deleted) {
                return ResponseEntity.ok("Thumbnail deleted successfully");
            } else {
                return new ResponseEntity<>("Failed to delete thumbnail", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception ex) {
            return new ResponseEntity<>("Failed to delete thumbnail: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * @deprecated Use /{id}/thumbnail instead
     * Delete product image by Product ID (RESTful style) - kept for backward compatibility
     */
    @Deprecated
    @DeleteMapping("/{id}/image")
    public ResponseEntity<?> deleteProductImageByProductId(@PathVariable("id") String id) {
        return deleteProductThumbnailByProductId(id);
    }

    /**
     * Delete product image by direct URL from MinIO (fallback endpoint)
     */
    @DeleteMapping("/image")
    public ResponseEntity<?> deleteProductImageByUrl(@RequestParam("url") String imageUrl) {
        try {
            boolean deleted = imageService.deleteImage(imageUrl);
            if (deleted) {
                return ResponseEntity.ok("Image deleted successfully");
            } else {
                return new ResponseEntity<>("Failed to delete image", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception ex) {
            return new ResponseEntity<>("Failed to delete image: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ---------- Helper Methods ----------

    private MediaType resolveMediaType(String imageUrl) {
        try {
            String guessed = URLConnection.guessContentTypeFromName(imageUrl);
            if (guessed != null) {
                return MediaType.parseMediaType(guessed);
            }
        } catch (Exception ignored) {
            // Fall through to default
        }
        return MediaType.APPLICATION_OCTET_STREAM;
    }
}
