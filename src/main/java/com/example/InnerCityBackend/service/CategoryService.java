package com.example.InnerCityBackend.service;

import com.example.InnerCityBackend.exception.BusinessException;
import com.example.InnerCityBackend.model.dto.request.CreateCategoryRequest;
import com.example.InnerCityBackend.model.dto.request.UpdateCategoryRequest;
import com.example.InnerCityBackend.model.dto.response.CategoryResponse;
import com.example.InnerCityBackend.model.dto.response.CategoryWithSubcategoriesResponse;
import com.example.InnerCityBackend.model.dto.response.SubcategoryResponse;
import com.example.InnerCityBackend.model.entity.Category;
import com.example.InnerCityBackend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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


    public List<CategoryWithSubcategoriesResponse> getAllWithSubcategories() {
        return categoryRepository.findAll().stream()
                .map(cat -> getWithSubs(cat.getId()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryWithSubcategoriesResponse getWithSubs(String id) {
        Category cat = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Category not found"));

        CategoryResponse parentDto = mapToResponse(cat);

        List<SubcategoryResponse> subDtos = cat.getSubcategories().stream()
                .map(sub -> SubcategoryResponse.builder()
                        .id(sub.getId())
                        .categoryId(cat.getId())
                        .name(sub.getName())
                        .description(sub.getDescription())
                        .category(parentDto)
                        .build())
                .toList();

        return CategoryWithSubcategoriesResponse.builder()
                .id(cat.getId())
                .name(cat.getName())
                .description(cat.getDescription())
                .subcategories(subDtos)
                .build();
    }

    private CategoryResponse mapToResponse(Category c) {
        return CategoryResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .description(c.getDescription())
                .build();
    }
}