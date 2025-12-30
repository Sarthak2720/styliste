package com.styliste.service;

import com.styliste.dto.UserDTO;
import com.styliste.entity.User;
import com.styliste.entity.UserRole;
import com.styliste.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Inside UserService.java

    public Page<UserDTO> getAllUsers(String role, Integer page, Integer pageSize) { // 👈 Added 'role' param
        log.debug("Fetching users. Role filter: {}", role);

        int pageNum = (page != null) ? page : 0;
        int size = (pageSize != null) ? pageSize : 10;
        Pageable pageable = PageRequest.of(pageNum, size, Sort.by("id").descending());

        Page<User> userPage;

        // Check if a role was provided
        if (role != null && !role.isEmpty()) {
            try {
                // Convert String to Enum (e.g., "CUSTOMER" -> UserRole.CUSTOMER)
                UserRole userRole = UserRole.valueOf(role.toUpperCase());
                userPage = userRepository.findByRole(userRole, pageable);
            } catch (IllegalArgumentException e) {
                // If they send an invalid role (e.g., "SUPERMAN"), return empty list or throw error
                log.warn("Invalid role requested: {}", role);
                return Page.empty();
            }
        } else {
            // No role filter? Return everyone.
            userPage = userRepository.findAll(pageable);
        }

        return userPage.map(this::mapToDTO);
    }

    private UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole().name())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                // Safe checks for null lists to avoid NullPointerExceptions
                .orderCount(user.getOrders() != null ? user.getOrders().size() : 0)
                .appointmentCount(user.getAppointments() != null ? user.getAppointments().size() : 0)
                .build();
    }
}