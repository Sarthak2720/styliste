package com.styliste.controller;

import com.styliste.dto.UserDTO;
import com.styliste.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDTO>> getAllUsers(
            @RequestParam(required = false) String role, // 👈 Capture the role from URL
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {

        log.info("Fetching users with Role: {}, Page: {}, Size: {}", role, page, pageSize);
        return ResponseEntity.ok(userService.getAllUsers(role, page, pageSize));
    }
}