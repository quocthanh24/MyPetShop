package com.thanhluu.tlcn.Controller.Employee.Product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thanhluu.tlcn.Annotation.ValidImage;
import com.thanhluu.tlcn.DTO.request.Product.ProductRequest;
import com.thanhluu.tlcn.DTO.response.Product.ProductResponse;
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
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/employees/products")
@Validated
@Slf4j
public class ProductController {
    @Autowired
    private IProductService productService;

    @Autowired
    private IImageService imageService;

    @Autowired
    private ObjectMapper objectMapper;

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
		public ResponseEntity<?> getProductsByCategory(@RequestParam String categoryName,Pageable pageable) {
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

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductRequest request) {
			ProductResponse product = productService.create(request);
			return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

		@PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
		public ResponseEntity<?> updateProduct(
			@RequestPart String productJson,
			@RequestPart MultipartFile file) {
			try {
				ProductRequest request = objectMapper.readValue(productJson, ProductRequest.class);
				if (file != null && !file.isEmpty()) {
					String imageUrl = imageService.uploadImage(file, "products");
					request.setImageURL(imageUrl);
				}
				return new ResponseEntity<>(productService.update(request), HttpStatus.CREATED);
			} catch (Exception ex) {
				return new ResponseEntity<>("Failed to create product: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

    /**
     * Create product with image (multipart/form-data)
     * Expects "data" as JSON string and optional "file" for image
     */
    @PostMapping(value = "/with-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProductWithImage(
            @RequestPart("data") String productJson,
            @RequestPart(value = "file", required = false) @ValidImage MultipartFile file
    ) {
        try {
            ProductRequest request = objectMapper.readValue(productJson, ProductRequest.class);
            if (file != null && !file.isEmpty()) {
                String imageUrl = imageService.uploadImage(file, "products");
                request.setImageURL(imageUrl);
            }
            return new ResponseEntity<>(productService.create(request), HttpStatus.CREATED);
        } catch (Exception ex) {
            return new ResponseEntity<>("Failed to create product: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
     * View product image by Product ID (RESTful style)
     * Gets image from MinIO using product's imageURL stored in database
     */
    @GetMapping("/{id}/image")
    public ResponseEntity<?> viewProductImageByProductId(@PathVariable("id") String id) {
        try {
            ProductResponse product = productService.findById(id);
            if (product == null || product.getImageURL() == null || product.getImageURL().isEmpty()) {
                return new ResponseEntity<>("Product image not found", HttpStatus.NOT_FOUND);
            }
            
            InputStream stream = imageService.getImage(product.getImageURL());
            InputStreamResource resource = new InputStreamResource(stream);
            MediaType mediaType = resolveMediaType(product.getImageURL());
            
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"product-" + id + "\"")
                    .body(resource);
        } catch (Exception ex) {
            return new ResponseEntity<>("Failed to load image: " + ex.getMessage(), HttpStatus.NOT_FOUND);
        }
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
     * Delete product image by Product ID (RESTful style)
     */
    @DeleteMapping("/{id}/image")
    public ResponseEntity<?> deleteProductImageByProductId(@PathVariable("id") String id) {
        try {
            ProductResponse product = productService.findById(id);
            if (product == null || product.getImageURL() == null || product.getImageURL().isEmpty()) {
                return new ResponseEntity<>("Product image not found", HttpStatus.NOT_FOUND);
            }
            
            boolean deleted = imageService.deleteImage(product.getImageURL());
            if (deleted) {
                return ResponseEntity.ok("Image deleted successfully");
            } else {
                return new ResponseEntity<>("Failed to delete image", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception ex) {
            return new ResponseEntity<>("Failed to delete image: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
