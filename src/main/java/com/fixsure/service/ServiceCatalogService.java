package com.fixsure.service;

import com.fixsure.dto.ServiceDto;
import com.fixsure.entity.Service;
import com.fixsure.exception.ResourceNotFoundException;
import com.fixsure.repository.CategoryRepository;
import com.fixsure.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceCatalogService {

    private final ServiceRepository serviceRepository;
    private final CategoryRepository categoryRepository;

    public List<ServiceDto.Summary> getAllServices(UUID categoryId, BigDecimal minPrice,
            BigDecimal maxPrice, BigDecimal minRating) {
        List<Service> services;
        if (categoryId == null && minPrice == null && maxPrice == null && minRating == null) {
            services = serviceRepository.findAll();
        } else {
            services = serviceRepository.findByFilters(categoryId, minPrice, maxPrice, minRating);
        }
        return services.stream().map(this::toSummary).toList();
    }

    public List<ServiceDto.Summary> searchServices(String query) {
        return serviceRepository.findByNameContainingIgnoreCase(query)
                .stream().map(this::toSummary).toList();
    }

    public ServiceDto.Detail getServiceById(UUID id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));
        return toDetail(service);
    }

    public ServiceDto.Detail createService(ServiceDto.Request request) {
        com.fixsure.entity.Category category = categoryRepository.findById(UUID.fromString(request.getCategoryId()))
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        Service service = Service.builder()
                .category(category)
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .basePrice(request.getBasePrice())
                .durationMinutes(request.getDurationMinutes())
                .includes(request.getIncludes() != null ? String.join(",", request.getIncludes()) : null)
                .build();
        return toDetail(serviceRepository.save(service));
    }

    public ServiceDto.Detail updateService(UUID id, ServiceDto.Request request) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));

        if (request.getCategoryId() != null) {
            com.fixsure.entity.Category category = categoryRepository.findById(UUID.fromString(request.getCategoryId()))
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
            service.setCategory(category);
        }

        if (request.getName() != null) service.setName(request.getName());
        if (request.getDescription() != null) service.setDescription(request.getDescription());
        if (request.getImageUrl() != null) service.setImageUrl(request.getImageUrl());
        if (request.getBasePrice() != null) service.setBasePrice(request.getBasePrice());
        if (request.getDurationMinutes() != null) service.setDurationMinutes(request.getDurationMinutes());
        if (request.getIncludes() != null) service.setIncludes(String.join(",", request.getIncludes()));

        return toDetail(serviceRepository.save(service));
    }

    public void deleteService(UUID id) {
        if (!serviceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Service not found with id: " + id);
        }
        serviceRepository.deleteById(id);
    }

    public ServiceDto.Summary toSummary(Service service) {
        return ServiceDto.Summary.builder()
                .id(service.getId().toString())
                .categoryId(service.getCategory().getId().toString())
                .categoryName(service.getCategory().getName())
                .name(service.getName())
                .imageUrl(service.getImageUrl())
                .basePrice(service.getBasePrice())
                .durationMinutes(service.getDurationMinutes())
                .rating(service.getRating())
                .reviewCount(service.getReviewCount())
                .build();
    }

    public ServiceDto.Detail toDetail(Service service) {
        List<String> includes = service.getIncludes() != null && !service.getIncludes().isBlank()
                ? List.of(service.getIncludes().split(","))
                : List.of();
        return ServiceDto.Detail.builder()
                .id(service.getId().toString())
                .categoryId(service.getCategory().getId().toString())
                .categoryName(service.getCategory().getName())
                .name(service.getName())
                .description(service.getDescription())
                .imageUrl(service.getImageUrl())
                .basePrice(service.getBasePrice())
                .durationMinutes(service.getDurationMinutes())
                .rating(service.getRating())
                .reviewCount(service.getReviewCount())
                .includes(includes)
                .build();
    }
}
