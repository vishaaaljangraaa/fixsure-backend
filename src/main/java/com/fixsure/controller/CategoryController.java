package com.fixsure.controller;

import com.fixsure.dto.ApiResponse;
import com.fixsure.dto.CategoryDto;
import com.fixsure.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Service categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Get all service categories")
    public ResponseEntity<ApiResponse<List<CategoryDto.Response>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(categoryService.getAllCategories()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a category by ID")
    public ResponseEntity<ApiResponse<CategoryDto.Response>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(categoryService.getCategoryById(id)));
    }

    @PostMapping
    @Operation(summary = "Create a new category")
    public ResponseEntity<ApiResponse<CategoryDto.Response>> createCategory(@RequestBody CategoryDto.Request request) {
        return ResponseEntity.ok(ApiResponse.ok(categoryService.createCategory(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing category")
    public ResponseEntity<ApiResponse<CategoryDto.Response>> updateCategory(@PathVariable UUID id, @RequestBody CategoryDto.Request request) {
        return ResponseEntity.ok(ApiResponse.ok(categoryService.updateCategory(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
