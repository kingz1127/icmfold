package com.example.InnerCityBackend.service;

import com.example.InnerCityBackend.exception.BusinessException;
import com.example.InnerCityBackend.model.dto.request.CreateCategoryRequest;
import com.example.InnerCityBackend.model.dto.request.UpdateCategoryRequest;
import com.example.InnerCityBackend.model.dto.response.CategoryResponse;
import com.example.InnerCityBackend.model.dto.response.CategoryWithSubcategoriesResponse;
import com.example.InnerCityBackend.model.dto.response.SubcategoryResponse;
import com.example.InnerCityBackend.model.entity.Category;
import com.example.InnerCityBackend.model.entity.Subcategory;
import com.example.InnerCityBackend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest req) {
        Category category = Category.builder().name(req.getName()).description(req.getDescription()).build();
        return mapToResponse(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse updateCategory(String id, UpdateCategoryRequest req) {
        Category cat = categoryRepository.findById(id).orElseThrow(() -> new BusinessException("Not found"));
        if (req.getName() != null) cat.setName(req.getName());
        if (req.getDescription() != null) cat.setDescription(req.getDescription());
        return mapToResponse(categoryRepository.save(cat));
    }

    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    public CategoryWithSubcategoriesResponse getWithSubs(String id) {
        Category cat = categoryRepository.findById(id).orElseThrow(() -> new BusinessException("Not found"));
        return CategoryWithSubcategoriesResponse.builder()
                .id(cat.getId()).name(cat.getName()).description(cat.getDescription())
                .subcategories(cat.getSubcategories().stream().map(this::mapToSubResponse).toList())
                .build();
    }

    private CategoryResponse mapToResponse(Category c) {
        return CategoryResponse.builder().id(c.getId()).name(c.getName()).description(c.getDescription()).build();
    }

    private SubcategoryResponse mapToSubResponse(Subcategory s) {
        return SubcategoryResponse.builder().id(s.getId()).name(s.getName()).description(s.getDescription()).categoryId(s.getCategory().getId()).build();
    }
}