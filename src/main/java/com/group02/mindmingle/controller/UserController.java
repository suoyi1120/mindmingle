package com.group02.mindmingle.controller;

import com.group02.mindmingle.dto.user.UserDTO;
import com.group02.mindmingle.model.User;
import com.group02.mindmingle.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/public/test")
    public ResponseEntity<String> publicEndpoint() {
        return ResponseEntity.ok("这是一个公开的端点，任何人都可以访问");
    }

    @GetMapping("/user/profile")
    public ResponseEntity<UserDTO> getUserProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        UserDTO userDTO = userService.convertToDto(user);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminEndpoint() {
        return ResponseEntity.ok("这是一个管理员端点，只有管理员可以访问");
    }
}
