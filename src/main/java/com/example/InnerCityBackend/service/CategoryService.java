//package com.example.InnerCityBackend.service;
//
//import com.example.InnerCityBackend.exception.BusinessException;
//import com.example.InnerCityBackend.model.dto.request.CreateCategoryRequest;
//import com.example.InnerCityBackend.model.dto.request.UpdateCategoryRequest;
//import com.example.InnerCityBackend.model.dto.response.CategoryResponse;
//import com.example.InnerCityBackend.model.dto.response.CategoryWithSubcategoriesResponse;
//import com.example.InnerCityBackend.model.dto.response.SubcategoryResponse;
//import com.example.InnerCityBackend.model.entity.Category;
//import com.example.InnerCityBackend.model.entity.Subcategory;
//import com.example.InnerCityBackend.repository.CategoryRepository;
//import com.example.InnerCityBackend.repository.SubcategoryRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class CategoryService {
//    private final CategoryRepository categoryRepository;
//    private final SubcategoryRepository subcategoryRepository;
//
//    @Transactional
//    public CategoryResponse createCategory(CreateCategoryRequest req) {
//        Category category = Category.builder().name(req.getName()).description(req.getDescription()).build();
//        return mapToResponse(categoryRepository.save(category));
//    }
//
//    @Transactional
//    public CategoryResponse updateCategory(String id, UpdateCategoryRequest req) {
//        Category cat = categoryRepository.findById(id).orElseThrow(() -> new BusinessException("Not found"));
//        if (req.getName() != null) cat.setName(req.getName());
//        if (req.getDescription() != null) cat.setDescription(req.getDescription());
//        return mapToResponse(categoryRepository.save(cat));
//    }
//
//    public List<CategoryResponse> getAll() {
//        return categoryRepository.findAll().stream().map(this::mapToResponse).toList();
//    }
//
//    @Transactional(readOnly = true)
//    public List<CategoryWithSubcategoriesResponse> getAllWithSubcategories() {
//        return categoryRepository.findAll().stream()
//                .map(cat -> getWithSubs(cat.getId()))
//                .collect(Collectors.toList());
//    }
//
//    @Transactional(readOnly = true)
//    public CategoryWithSubcategoriesResponse getWithSubs(String id) {
//        Category cat = categoryRepository.findById(id)
//                .orElseThrow(() -> new BusinessException("Category not found"));
//
//        CategoryResponse parentDto = mapToResponse(cat);
//
//        List<SubcategoryResponse> subDtos = cat.getSubcategories().stream()
//                .map(sub -> SubcategoryResponse.builder()
//                        .id(sub.getId())
//                        .categoryId(cat.getId())
//                        .name(sub.getName())
//                        .description(sub.getDescription())
////                        .category(parentDto)
//                        .build())
//                .toList();
//
//        return CategoryWithSubcategoriesResponse.builder()
//                .id(cat.getId())
//                .name(cat.getName())
//                .description(cat.getDescription())
//                .subcategories(subDtos)
//                .build();
//    }
//
//    private CategoryResponse mapToResponse(Category c) {
//        return CategoryResponse.builder()
//                .id(c.getId())
//                .name(c.getName())
//                .description(c.getDescription())
//                .build();
//    }
//
//    @Transactional
//    public void deleteCategory(String id) {
//        // First check if any subcategory has outreaches
//        boolean hasOutreaches = subcategoryRepository.existsSubcategoryWithOutreachesByCategoryId(id);
//
//        if (hasOutreaches) {
//            throw new BusinessException(
//                    "Cannot delete category because some subcategories have associated outreaches. " +
//                            "Please delete the outreaches first."
//            );
//        }
//
//        // Check if category has any subcategories
//        boolean hasSubcategories = subcategoryRepository.existsByCategoryId(id);
//
//        if (hasSubcategories) {
//            // Delete all subcategories first
//            List<Subcategory> subcategories = subcategoryRepository.findByCategoryId(id);
//            subcategoryRepository.deleteAll(subcategories);
//        }
//
//        // Then delete the category
//        categoryRepository.deleteById(id);
//    }
//
//    @Transactional(readOnly = true)
//    public List<CategoryWithSubcategoriesResponse> getAllWithSubs() {
//        return categoryRepository.findAll()
//                .stream()
//                .map(category -> getWithSubs(category.getId()))
//                .collect(Collectors.toList());
//    }
//}




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
import com.example.InnerCityBackend.repository.SubcategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final SubcategoryRepository subcategoryRepository;

    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest req) {
        Category category = Category.builder()
                .name(req.getName())
                .description(req.getDescription())
                .build();
        return mapToResponse(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse updateCategory(String id, UpdateCategoryRequest req) {
        Category cat = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Category not found with id: " + id));

        if (req.getName() != null && !req.getName().trim().isEmpty()) {
            cat.setName(req.getName());
        }
        if (req.getDescription() != null) {
            cat.setDescription(req.getDescription());
        }

        return mapToResponse(categoryRepository.save(cat));
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CategoryWithSubcategoriesResponse> getAllWithSubcategories() {
        try {
            List<Category> categories = categoryRepository.findAll();
            log.info("Fetching {} categories with subcategories", categories.size());

            return categories.stream()
                    .map(cat -> {
                        log.debug("Processing category: {} - {}", cat.getId(), cat.getName());
                        return getWithSubs(cat.getId());
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error in getAllWithSubcategories: {}", e.getMessage(), e);
            throw new BusinessException("Failed to fetch categories with subcategories: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public CategoryWithSubcategoriesResponse getWithSubs(String id) {
        try {
            Category cat = categoryRepository.findById(id)
                    .orElseThrow(() -> new BusinessException("Category not found with id: " + id));

            // Safely handle null subcategories
            List<Subcategory> subcategories = cat.getSubcategories();
            if (subcategories == null) {
                subcategories = new ArrayList<>();
            }

            List<SubcategoryResponse> subDtos = subcategories.stream()
                    .map(sub -> SubcategoryResponse.builder()
                            .id(sub.getId())
                            .categoryId(cat.getId())
                            .name(sub.getName())
                            .description(sub.getDescription())
                            .build())
                    .toList();

            log.debug("Found {} subcategories for category: {}", subDtos.size(), cat.getName());

            return CategoryWithSubcategoriesResponse.builder()
                    .id(cat.getId())
                    .name(cat.getName())
                    .description(cat.getDescription())
                    .subcategories(subDtos)
                    .build();

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error in getWithSubs for id {}: {}", id, e.getMessage(), e);
            throw new BusinessException("Failed to fetch category with subcategories: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<CategoryWithSubcategoriesResponse> getAllWithSubs() {
        return getAllWithSubcategories(); // Reuse the same method to avoid duplication
    }

    @Transactional
    public void deleteCategory(String id) {
        try {
            // First check if category exists
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new BusinessException("Category not found with id: " + id));

            // Check if any subcategory has outreaches
            boolean hasOutreaches = subcategoryRepository.existsSubcategoryWithOutreachesByCategoryId(id);

            if (hasOutreaches) {
                throw new BusinessException(
                        "Cannot delete category '" + category.getName() + "' because some subcategories have associated outreaches. " +
                                "Please delete the outreaches first."
                );
            }

            // Check if category has any subcategories
            boolean hasSubcategories = subcategoryRepository.existsByCategoryId(id);

            if (hasSubcategories) {
                log.info("Deleting {} subcategories for category: {}",
                        subcategoryRepository.countByCategoryId(id), category.getName());

                // Delete all subcategories first
                List<Subcategory> subcategories = subcategoryRepository.findByCategoryId(id);
                subcategoryRepository.deleteAll(subcategories);
            }

            // Then delete the category
            categoryRepository.deleteById(id);
            log.info("Successfully deleted category: {} ({})", category.getName(), id);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error deleting category {}: {}", id, e.getMessage(), e);
            throw new BusinessException("Failed to delete category: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Category not found with id: " + id));
        return mapToResponse(category);
    }

    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return categoryRepository.findByName(name).isPresent();
    }

    private CategoryResponse mapToResponse(Category c) {
        if (c == null) {
            return null;
        }
        return CategoryResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .description(c.getDescription())
                .build();
    }
}