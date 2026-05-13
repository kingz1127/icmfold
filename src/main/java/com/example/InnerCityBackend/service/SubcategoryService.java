
package com.example.InnerCityBackend.service;

import com.example.InnerCityBackend.exception.BusinessException;
import com.example.InnerCityBackend.model.dto.request.CreateSubcategoryRequest;
import com.example.InnerCityBackend.model.dto.request.UpdateSubcategoryRequest;
import com.example.InnerCityBackend.model.dto.response.CategoryResponse;
import com.example.InnerCityBackend.model.dto.response.SubcategoryResponse;
import com.example.InnerCityBackend.model.entity.Category;
import com.example.InnerCityBackend.model.entity.Subcategory;
import com.example.InnerCityBackend.repository.CategoryRepository;
import com.example.InnerCityBackend.repository.OutreachRepository;
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
public class SubcategoryService {
    private final SubcategoryRepository subcategoryRepository;
    private final CategoryRepository categoryRepository;
    private final OutreachRepository outreachRepository;

    @Transactional
    public SubcategoryResponse create(CreateSubcategoryRequest req) {
        try {
            log.info("Creating new subcategory: {}", req.getName());

            // Validate input
            if (req.getName() == null || req.getName().trim().isEmpty()) {
                throw new BusinessException("Subcategory name cannot be empty");
            }

            Category cat = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new BusinessException("Category not found with id: " + req.getCategoryId()));

            // Check if subcategory with same name exists in this category
            boolean exists = subcategoryRepository.existsByCategoryIdAndName(req.getCategoryId(), req.getName());
            if (exists) {
                throw new BusinessException("Subcategory with name '" + req.getName() + "' already exists in this category");
            }

            Subcategory sub = Subcategory.builder()
                    .name(req.getName().trim())
                    .description(req.getDescription())
                    .category(cat)
                    .build();

            Subcategory saved = subcategoryRepository.save(sub);
            log.info("Successfully created subcategory: {} with id: {}", saved.getName(), saved.getId());

            return mapToResponse(saved);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error creating subcategory: {}", e.getMessage(), e);
            throw new BusinessException("Failed to create subcategory: " + e.getMessage());
        }
    }

    @Transactional
    public SubcategoryResponse update(String id, UpdateSubcategoryRequest req) {
        try {
            log.info("Updating subcategory with id: {}", id);

            Subcategory sub = subcategoryRepository.findById(id)
                    .orElseThrow(() -> new BusinessException("Subcategory not found with id: " + id));

            if (req.getName() != null && !req.getName().trim().isEmpty()) {
                // Check if new name conflicts with existing subcategory in same category
                String categoryId = req.getCategoryId() != null ? req.getCategoryId() : sub.getCategory().getId();
                boolean exists = subcategoryRepository.existsByCategoryIdAndNameAndIdNot(
                        categoryId, req.getName().trim(), id);

                if (exists) {
                    throw new BusinessException("Subcategory with name '" + req.getName() + "' already exists in this category");
                }
                sub.setName(req.getName().trim());
            }

            if (req.getDescription() != null) {
                sub.setDescription(req.getDescription());
            }

            if (req.getCategoryId() != null) {
                Category cat = categoryRepository.findById(req.getCategoryId())
                        .orElseThrow(() -> new BusinessException("Category not found with id: " + req.getCategoryId()));
                sub.setCategory(cat);
            }

            Subcategory updated = subcategoryRepository.save(sub);
            log.info("Successfully updated subcategory: {} with id: {}", updated.getName(), updated.getId());

            return mapToResponse(updated);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating subcategory {}: {}", id, e.getMessage(), e);
            throw new BusinessException("Failed to update subcategory: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<SubcategoryResponse> getAll() {
        try {
            log.debug("Fetching all subcategories");
            List<Subcategory> subcategories = subcategoryRepository.findAll();

            if (subcategories.isEmpty()) {
                log.debug("No subcategories found");
                return new ArrayList<>();
            }

            log.debug("Found {} subcategories", subcategories.size());
            return subcategories.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error fetching all subcategories: {}", e.getMessage(), e);
            throw new BusinessException("Failed to fetch subcategories: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<SubcategoryResponse> getByCategoryId(String categoryId) {
        try {
            log.debug("Fetching subcategories for category id: {}", categoryId);

            // Verify category exists
            if (!categoryRepository.existsById(categoryId)) {
                throw new BusinessException("Category not found with id: " + categoryId);
            }

            List<Subcategory> subcategories = subcategoryRepository.findByCategoryId(categoryId);

            if (subcategories.isEmpty()) {
                log.debug("No subcategories found for category: {}", categoryId);
                return new ArrayList<>();
            }

            log.debug("Found {} subcategories for category: {}", subcategories.size(), categoryId);
            return subcategories.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching subcategories for category {}: {}", categoryId, e.getMessage(), e);
            throw new BusinessException("Failed to fetch subcategories for category: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public SubcategoryResponse getById(String id) {
        try {
            log.debug("Fetching subcategory with id: {}", id);

            Subcategory subcategory = subcategoryRepository.findById(id)
                    .orElseThrow(() -> new BusinessException("Subcategory not found with id: " + id));

            return mapToResponse(subcategory);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching subcategory {}: {}", id, e.getMessage(), e);
            throw new BusinessException("Failed to fetch subcategory: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteSubcategory(String id) {
        try {
            log.info("Deleting subcategory with id: {}", id);

            // Check if subcategory exists
            Subcategory subcategory = subcategoryRepository.findById(id)
                    .orElseThrow(() -> new BusinessException("Subcategory not found with id: " + id));

            // Check if subcategory has any outreaches
            boolean hasOutreaches = outreachRepository.existsBySubcategoryId(id);

            if (hasOutreaches) {
                long outreachCount = outreachRepository.countBySubcategoryId(id);
                throw new BusinessException(
                        String.format("Cannot delete subcategory '%s' because it has %d outreach(s) associated with it. " +
                                        "Please delete or reassign the outreaches first.",
                                subcategory.getName(), outreachCount)
                );
            }

            subcategoryRepository.deleteById(id);
            log.info("Successfully deleted subcategory: {} ({})", subcategory.getName(), id);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error deleting subcategory {}: {}", id, e.getMessage(), e);
            throw new BusinessException("Failed to delete subcategory: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public long countByCategoryId(String categoryId) {
        try {
            return subcategoryRepository.countByCategoryId(categoryId);
        } catch (Exception e) {
            log.error("Error counting subcategories for category {}: {}", categoryId, e.getMessage(), e);
            throw new BusinessException("Failed to count subcategories: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public boolean existsByNameInCategory(String categoryId, String name) {
        try {
            return subcategoryRepository.existsByCategoryIdAndName(categoryId, name);
        } catch (Exception e) {
            log.error("Error checking subcategory existence: {}", e.getMessage(), e);
            throw new BusinessException("Failed to check subcategory existence: " + e.getMessage());
        }
    }

    private SubcategoryResponse mapToResponse(Subcategory s) {
        if (s == null) {
            return null;
        }

        return SubcategoryResponse.builder()
                .id(s.getId())
                .name(s.getName())
                .description(s.getDescription())
                .categoryId(s.getCategory() != null ? s.getCategory().getId() : null)
                .build();
    }
}