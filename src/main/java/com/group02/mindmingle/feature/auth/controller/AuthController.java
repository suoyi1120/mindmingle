package com.group02.mindmingle.feature.auth.controller;

import com.group02.mindmingle.feature.auth.dto.JwtResponse;
import com.group02.mindmingle.feature.auth.dto.LoginRequest;
import com.group02.mindmingle.feature.auth.dto.RegisterRequest;
import com.group02.mindmingle.feature.auth.dto.RegisterResponse;
import com.group02.mindmingle.feature.auth.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
                JwtResponse jwtResponse = authService.authenticateUser(loginRequest);

                // 创建一个HTTP Only Cookie
                Cookie jwtCookie = new Cookie("jwt", jwtResponse.getToken());
                jwtCookie.setHttpOnly(true);
                jwtCookie.setSecure(true); // 在生产环境中使用HTTPS时启用
                jwtCookie.setPath("/");
                jwtCookie.setMaxAge(86400); // 设置为与JWT令牌相同的过期时间

                // 添加Cookie到响应中
                response.addCookie(jwtCookie);

                // 返回用户信息，但不包含token
                jwtResponse.setToken(null);
                return ResponseEntity.ok(jwtResponse);
        }

        @PostMapping("/register")
        public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
                RegisterResponse response = authService.registerUser(registerRequest);
                return ResponseEntity.ok(response);
        }

        @PostMapping("/logout")
        public ResponseEntity<?> logoutUser(HttpServletResponse response) {
                // 创建一个空的过期Cookie来删除客户端的JWT Cookie
                Cookie jwtCookie = new Cookie("jwt", null);
                jwtCookie.setHttpOnly(true);
                jwtCookie.setSecure(true);
                jwtCookie.setPath("/");
                jwtCookie.setMaxAge(0); // 立即过期

                response.addCookie(jwtCookie);

                return ResponseEntity.ok().body(RegisterResponse.builder()
                                .message("用户已成功登出")
                                .success(true)
                                .userId(null)
                                .build());
        }
}
