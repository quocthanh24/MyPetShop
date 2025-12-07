package com.thanhluu.tlcn.DTO.response.Category;

import lombok.Data;

import java.util.UUID;

@Data
public class CategoryResponse {

    private UUID id;

    private String name;

    private String description;
}
