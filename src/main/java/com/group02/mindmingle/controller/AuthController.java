package com.group02.mindmingle.controller;

import com.group02.mindmingle.dto.auth.LoginRequest;
import com.group02.mindmingle.dto.auth.RegisterRequest;
import com.group02.mindmingle.dto.auth.RegisterResponse;
import com.group02.mindmingle.dto.user.UserDTO;
import com.group02.mindmingle.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

        private final AuthService authService;

        public AuthController(AuthService authService) {
                this.authService = authService;
        }

        @PostMapping("/login")
        public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
                        HttpServletResponse response) {
                // 使用新的login方法，直接返回UserDTO
                UserDTO userDTO = authService.login(loginRequest, response);
                return ResponseEntity.ok(userDTO);
        }

        @PostMapping("/register")
        public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
                RegisterResponse response = authService.registerUser(registerRequest);
                return ResponseEntity.ok(response);
        }

        @PostMapping("/logout")
        public ResponseEntity<?> logoutUser(HttpServletResponse response) {
                // 使用AuthService中的方法清除cookie
                authService.clearAuthCookie(response);

                return ResponseEntity.ok().body(RegisterResponse.builder()
                                .message("用户已成功登出")
                                .success(true)
                                .userId(null)
                                .build());
        }
}
