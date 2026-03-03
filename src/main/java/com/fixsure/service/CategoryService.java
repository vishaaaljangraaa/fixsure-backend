package com.fixsure.service;

import com.fixsure.dto.CategoryDto;
import com.fixsure.entity.Category;
import com.fixsure.exception.ResourceNotFoundException;
import com.fixsure.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryDto.Response> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public CategoryDto.Response getCategoryById(UUID id) {
        return toResponse(categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id)));
    }

    public CategoryDto.Response toResponse(Category category) {
        return CategoryDto.Response.builder()
                .id(category.getId().toString())
                .name(category.getName())
                .description(category.getDescription())
                .iconUrl(category.getIconUrl())
                .build();
    }
}
