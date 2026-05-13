package com.example.InnerCityBackend.controller;

import com.example.InnerCityBackend.model.dto.request.CreateCategoryRequest;
import com.example.InnerCityBackend.model.dto.request.UpdateCategoryRequest;
import com.example.InnerCityBackend.model.dto.response.CategoryResponse;
import com.example.InnerCityBackend.model.dto.response.CategoryWithSubcategoriesResponse;
import com.example.InnerCityBackend.model.dto.response.SuccessResponse;
import com.example.InnerCityBackend.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("categories")
@RequiredArgsConstructor
@Tag(name = "Category Controller", description = "Admin acess")
public class CategoryController {

    private final CategoryService categoryService;

    // PUBLIC: List all categories (id, name, description)
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    // PUBLIC: Get one category including all its nested subcategories
    @GetMapping("/{id}")
    public ResponseEntity<CategoryWithSubcategoriesResponse> getCategoryWithSubcategories(@PathVariable String id) {
        return ResponseEntity.ok(categoryService.getWithSubs(id));
    }

    // ADMIN: Create a category
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        return ResponseEntity.ok(categoryService.createCategory(request));
    }

    // ADMIN: Update a category
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable String id,
            @RequestBody UpdateCategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    @GetMapping("/categories-with-subcategories")
    public ResponseEntity<List<CategoryWithSubcategoriesResponse>> getAllCategoriesWithSubcategories() {
        return ResponseEntity.ok(categoryService.getAllWithSubs());
    }

    // ADMIN: Delete a category
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse> deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(new SuccessResponse("Category deleted successfully"));
    }
}