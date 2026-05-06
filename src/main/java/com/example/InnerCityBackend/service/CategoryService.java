package com.example.InnerCityBackend.service;

import com.example.InnerCityBackend.exception.BusinessException;
import com.example.InnerCityBackend.model.dto.request.CreateCategoryRequest;
import com.example.InnerCityBackend.model.entity.Category;
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
    public Category save(CreateCategoryRequest req) {
        // 1. Check if a category with this name already exists to avoid duplicates
        if (categoryRepository.existsByName(req.getName())) {
            throw new BusinessException("Category with this name already exists");
        }

        // 2. Map Request DTO to Entity using Builder
        Category category = Category.builder()
                .name(req.getName())
                .description(req.getDescription())
                .build();

        // 3. Save to database
        return categoryRepository.save(category);
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }
}