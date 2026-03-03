package com.fixsure.service;

import com.fixsure.dto.ServiceDto;
import com.fixsure.entity.Service;
import com.fixsure.exception.ResourceNotFoundException;
import com.fixsure.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceCatalogService {

    private final ServiceRepository serviceRepository;

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
