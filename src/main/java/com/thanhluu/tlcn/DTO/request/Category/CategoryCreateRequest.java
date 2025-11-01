package com.thanhluu.tlcn.DTO.request.Category;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CategoryCreateRequest {

    @NotEmpty(message = "Name should not be null")
    private String name;

    private String description;

}
