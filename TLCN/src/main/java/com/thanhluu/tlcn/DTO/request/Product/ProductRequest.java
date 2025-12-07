package com.thanhluu.tlcn.DTO.request.Product;

import com.thanhluu.tlcn.Enum.PetType;
import com.thanhluu.tlcn.Enum.ProductStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    private String id; // For update operations
    @NotNull(message = "Name should not be null")
    private String name;
    @PositiveOrZero(message = "Price should be greater or equal 0")
    private Double price;
    private String thumbnailUrl;
    @PositiveOrZero(message = "Stock quantity should be greater or equal 0")
    private Integer stockQuantity;
    @NotNull(message = "Product status should not be null")
    private ProductStatus status;
    @NotNull(message = "Pet type of product should not be null")
    private PetType petType;
    @NotEmpty(message = "Category name should not empty")
    private String categoryName;
}
