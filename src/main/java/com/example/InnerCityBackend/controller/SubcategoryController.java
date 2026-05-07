package com.example.InnerCityBackend.controller;

import com.example.InnerCityBackend.model.dto.request.CreateSubcategoryRequest;
import com.example.InnerCityBackend.model.dto.request.UpdateSubcategoryRequest;
import com.example.InnerCityBackend.model.dto.response.SubcategoryResponse;
import com.example.InnerCityBackend.model.dto.response.SuccessResponse;
import com.example.InnerCityBackend.service.SubcategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subcategories")
@RequiredArgsConstructor
@Tag(name = "SubCategory Controller", description = "Admin access")
public class SubcategoryController {

    private final SubcategoryService subcategoryService;

    // PUBLIC: Get all subcategories
    @GetMapping
    public ResponseEntity<List<SubcategoryResponse>> getAllSubcategories() {
        return ResponseEntity.ok(subcategoryService.getAll());
    }

    // PUBLIC: Get subcategories belonging to a specific category
    // Useful for React Native dropdowns
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<SubcategoryResponse>> getByCategoryId(@PathVariable String categoryId) {
        return ResponseEntity.ok(subcategoryService.getByCategoryId(categoryId));
    }

    // ADMIN: Create a subcategory
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubcategoryResponse> createSubcategory(@Valid @RequestBody CreateSubcategoryRequest request) {
        return ResponseEntity.ok(subcategoryService.create(request));
    }

    // ADMIN: Update a subcategory
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubcategoryResponse> updateSubcategory(
            @PathVariable String id,
            @RequestBody UpdateSubcategoryRequest request) {
        return ResponseEntity.ok(subcategoryService.update(id, request));
    }

    // ADMIN: Delete
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse> deleteSubcategory(@PathVariable String id) {
        subcategoryService.deleteSubcategory(id);
        return ResponseEntity.ok(new SuccessResponse("Subcategory deleted successfully"));
    }
}