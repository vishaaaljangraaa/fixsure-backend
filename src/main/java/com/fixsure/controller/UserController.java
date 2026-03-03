package com.fixsure.controller;

import com.fixsure.dto.AddressDto;
import com.fixsure.dto.ApiResponse;
import com.fixsure.dto.UserDto;
import com.fixsure.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User profile and address management")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "Register a new user")
    public ResponseEntity<ApiResponse<UserDto.Response>> createUser(
            @Valid @RequestBody UserDto.CreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("User created successfully", userService.createUser(req)));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user profile by ID")
    public ResponseEntity<ApiResponse<UserDto.Response>> getUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getUserById(userId)));
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Update user profile")
    public ResponseEntity<ApiResponse<UserDto.Response>> updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody UserDto.UpdateRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("User updated", userService.updateUser(userId, req)));
    }

    @GetMapping("/{userId}/addresses")
    @Operation(summary = "Get all saved addresses for a user")
    public ResponseEntity<ApiResponse<List<AddressDto.Response>>> getAddresses(@PathVariable UUID userId) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getAddresses(userId)));
    }

    @PostMapping("/{userId}/addresses")
    @Operation(summary = "Add a new address for a user")
    public ResponseEntity<ApiResponse<AddressDto.Response>> addAddress(
            @PathVariable UUID userId,
            @Valid @RequestBody AddressDto.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Address added", userService.addAddress(userId, req)));
    }

    @DeleteMapping("/{userId}/addresses/{addressId}")
    @Operation(summary = "Delete an address")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @PathVariable UUID userId,
            @PathVariable UUID addressId) {
        userService.deleteAddress(userId, addressId);
        return ResponseEntity.ok(ApiResponse.ok("Address deleted", null));
    }
}
