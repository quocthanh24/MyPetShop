package com.thanhluu.tlcn.DTO.response.Product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResp {
    private UUID id;
    private String name;
    private Double price;
    private String thumbnailUrl;
    private Integer stockQuantity;
    private String status;
    private String petType;
    private String categoryName;
    private List<String> imageUrls;
}
