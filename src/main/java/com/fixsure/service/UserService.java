package com.fixsure.service;

import com.fixsure.dto.AddressDto;
import com.fixsure.dto.UserDto;
import com.fixsure.entity.Address;
import com.fixsure.entity.User;
import com.fixsure.exception.ResourceNotFoundException;
import com.fixsure.repository.AddressRepository;
import com.fixsure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    @Transactional
    public UserDto.Response createUser(UserDto.CreateRequest req) {
        User user = User.builder()
                .name(req.getName())
                .phone(req.getPhone())
                .email(req.getEmail())
                .profilePhotoUrl(req.getProfilePhotoUrl())
                .build();
        return toResponse(userRepository.save(user));
    }

    public UserDto.Response getUserById(UUID userId) {
        return toResponse(findUserOrThrow(userId));
    }

    @Transactional
    public UserDto.Response updateUser(UUID userId, UserDto.UpdateRequest req) {
        User user = findUserOrThrow(userId);
        if (req.getName() != null)
            user.setName(req.getName());
        if (req.getEmail() != null)
            user.setEmail(req.getEmail());
        if (req.getProfilePhotoUrl() != null)
            user.setProfilePhotoUrl(req.getProfilePhotoUrl());
        return toResponse(userRepository.save(user));
    }

    public List<AddressDto.Response> getAddresses(UUID userId) {
        findUserOrThrow(userId); // ensure user exists
        return addressRepository.findByUserId(userId)
                .stream().map(this::toAddressResponse).collect(Collectors.toList());
    }

    @Transactional
    public AddressDto.Response addAddress(UUID userId, AddressDto.Request req) {
        User user = findUserOrThrow(userId);

        // If this is set as default, unset all previous defaults
        if (Boolean.TRUE.equals(req.getIsDefault())) {
            addressRepository.findByUserIdAndIsDefaultTrue(userId)
                    .forEach(addr -> {
                        addr.setIsDefault(false);
                        addressRepository.save(addr);
                    });
        }

        Address address = Address.builder()
                .user(user)
                .line1(req.getLine1())
                .line2(req.getLine2())
                .city(req.getCity())
                .state(req.getState())
                .pinCode(req.getPinCode())
                .isDefault(req.getIsDefault())
                .build();
        return toAddressResponse(addressRepository.save(address));
    }

    @Transactional
    public void deleteAddress(UUID userId, UUID addressId) {
        findUserOrThrow(userId);
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        addressRepository.delete(address);
    }

    // ---- Helpers ----

    private User findUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    public UserDto.Response toResponse(User user) {
        return UserDto.Response.builder()
                .id(user.getId().toString())
                .name(user.getName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .profilePhotoUrl(user.getProfilePhotoUrl())
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null)
                .build();
    }

    public AddressDto.Response toAddressResponse(Address address) {
        return AddressDto.Response.builder()
                .id(address.getId().toString())
                .line1(address.getLine1())
                .line2(address.getLine2())
                .city(address.getCity())
                .state(address.getState())
                .pinCode(address.getPinCode())
                .isDefault(address.getIsDefault())
                .build();
    }
}
