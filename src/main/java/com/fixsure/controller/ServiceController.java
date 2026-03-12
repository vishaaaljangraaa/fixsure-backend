package com.fixsure.controller;

import com.fixsure.dto.ApiResponse;
import com.fixsure.dto.ServiceDto;
import com.fixsure.service.ServiceCatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
@Tag(name = "Services", description = "Service catalog")
public class ServiceController {

    private final ServiceCatalogService serviceCatalogService;

    @GetMapping
    @Operation(summary = "List all services with optional filters")
    public ResponseEntity<ApiResponse<List<ServiceDto.Summary>>> getAll(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) BigDecimal minRating,
            @RequestParam(required = false) String search) {

        List<ServiceDto.Summary> result;
        if (search != null && !search.isBlank()) {
            result = serviceCatalogService.searchServices(search);
        } else {
            result = serviceCatalogService.getAllServices(categoryId, minPrice, maxPrice, minRating);
        }
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get full service details by ID")
    public ResponseEntity<ApiResponse<ServiceDto.Detail>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(serviceCatalogService.getServiceById(id)));
    }

    @PostMapping
    @Operation(summary = "Create a new service")
    public ResponseEntity<ApiResponse<ServiceDto.Detail>> createService(@RequestBody ServiceDto.Request request) {
        return ResponseEntity.ok(ApiResponse.ok(serviceCatalogService.createService(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing service")
    public ResponseEntity<ApiResponse<ServiceDto.Detail>> updateService(@PathVariable UUID id, @RequestBody ServiceDto.Request request) {
        return ResponseEntity.ok(ApiResponse.ok(serviceCatalogService.updateService(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a service")
    public ResponseEntity<ApiResponse<Void>> deleteService(@PathVariable UUID id) {
        serviceCatalogService.deleteService(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
