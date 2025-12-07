package com.thanhluu.tlcn.Controller.Employee.Product;

import com.thanhluu.tlcn.DTO.request.Category.CreateCategoryRequest;
import com.thanhluu.tlcn.Service.Employee.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees/categories")
public class CategoryController {
    @Autowired
    private ICategoryService categoryService;

    @PostMapping("/create")
    public ResponseEntity<?> createCategory(@RequestBody @Validated CreateCategoryRequest category_CreateResp) {
        return new ResponseEntity<>(categoryService.save(category_CreateResp), HttpStatus.CREATED);
    }

    // Lấy danh sách category
    @GetMapping
    public ResponseEntity<?> getAllCategories(@PageableDefault(size = 10, page = 0) Pageable pageable ) {
        return new  ResponseEntity<>(categoryService.findAll(pageable), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateCategory(
                @RequestBody @Validated CreateCategoryRequest category_CreateResp,
                @PathVariable String id
                                            ) {
        return new ResponseEntity<>(categoryService.update(category_CreateResp, id), HttpStatus.OK);
    }
}
