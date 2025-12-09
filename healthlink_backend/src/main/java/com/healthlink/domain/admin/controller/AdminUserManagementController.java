package com.healthlink.domain.admin.controller;

import com.healthlink.domain.user.dto.UserDto;
import com.healthlink.domain.user.entity.User;
import com.healthlink.domain.user.enums.UserRole;
import com.healthlink.domain.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin User Management", description = "Admin endpoints for managing all users")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserManagementController {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @GetMapping
    @Operation(summary = "Get all users, optionally filtered by role")
    public ResponseEntity<List<UserDto>> getAllUsers(@RequestParam(required = false) String role) {
        List<User> users;
        
        if (role != null && !role.isEmpty()) {
            try {
                UserRole userRole = UserRole.valueOf(role.toUpperCase());
                users = userRepository.findByRole(userRole).stream()
                        .filter(user -> user.getDeletedAt() == null)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                users = userRepository.findAll().stream()
                        .filter(user -> user.getDeletedAt() == null)
                        .collect(Collectors.toList());
            }
        } else {
            users = userRepository.findAll().stream()
                    .filter(user -> user.getDeletedAt() == null)
                    .collect(Collectors.toList());
        }
        
        List<UserDto> userDtos = users.stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(userDtos);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<UserDto> getUserById(@PathVariable UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getDeletedAt() != null) {
            throw new RuntimeException("User not found");
        }
        
        return ResponseEntity.ok(modelMapper.map(user, UserDto.class));
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete (soft delete) a user")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.softDelete();
        userRepository.save(user);
        
        return ResponseEntity.noContent().build();
    }
}

