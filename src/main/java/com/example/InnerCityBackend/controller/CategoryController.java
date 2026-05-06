package com.example.InnerCityBackend.controller;

import com.example.InnerCityBackend.model.dto.request.CreateCategoryRequest;
import com.example.InnerCityBackend.model.entity.Category;
import com.example.InnerCityBackend.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("categories")
@RequiredArgsConstructor
@Tag(name = "Category Controller", description = "Admin has right")

public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<Category> list() {
        return categoryService.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Category create(@RequestBody CreateCategoryRequest req) {
        return categoryService.save(req);
    }
}
