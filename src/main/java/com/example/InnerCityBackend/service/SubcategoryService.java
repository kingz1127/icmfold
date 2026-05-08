package com.example.InnerCityBackend.service;

import com.example.InnerCityBackend.exception.BusinessException;
import com.example.InnerCityBackend.model.dto.request.CreateSubcategoryRequest;
import com.example.InnerCityBackend.model.dto.request.UpdateSubcategoryRequest;
import com.example.InnerCityBackend.model.dto.response.CategoryResponse;
import com.example.InnerCityBackend.model.dto.response.SubcategoryResponse;
import com.example.InnerCityBackend.model.entity.Category;
import com.example.InnerCityBackend.model.entity.Subcategory;
import com.example.InnerCityBackend.repository.CategoryRepository;
import com.example.InnerCityBackend.repository.SubcategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubcategoryService {
    private final SubcategoryRepository subRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public SubcategoryResponse create(CreateSubcategoryRequest req) {
        Category cat = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new BusinessException("Category not found"));

        Subcategory sub = Subcategory.builder()
                .name(req.getName())
                .description(req.getDescription())
                .category(cat)
                .build();

        return mapToResponse(subRepository.save(sub));
    }

    @Transactional
    public SubcategoryResponse update(String id, UpdateSubcategoryRequest req) {
        Subcategory sub = subRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Subcategory not found"));

        if (req.getName() != null) sub.setName(req.getName());
        if (req.getDescription() != null) sub.setDescription(req.getDescription());

        if (req.getCategoryId() != null) {
            Category cat = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new BusinessException("Category not found"));
            sub.setCategory(cat);
        }

        return mapToResponse(subRepository.save(sub));
    }

    public List<SubcategoryResponse> getAll() {
        return subRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<SubcategoryResponse> getByCategoryId(String categoryId) {
        return subRepository.findByCategoryId(categoryId).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public void deleteSubcategory(String id) {
        subRepository.deleteById(id);
    }

    private SubcategoryResponse mapToResponse(Subcategory s) {
        // Create the CategoryResponse object to satisfy the DTO type
        CategoryResponse catDto = CategoryResponse.builder()
                .id(s.getCategory().getId())
                .name(s.getCategory().getName())
                .description(s.getCategory().getDescription())
                .build();

        return SubcategoryResponse.builder()
                .id(s.getId())
                .name(s.getName())
                .description(s.getDescription())
                .categoryId(s.getCategory().getId())
                .category(catDto)
                .build();
    }
}