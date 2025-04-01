package com.group02.mindmingle.service;

import com.group02.mindmingle.common.security.JwtTokenUtil;
import com.group02.mindmingle.dto.auth.LoginRequest;
import com.group02.mindmingle.dto.auth.RegisterRequest;
import com.group02.mindmingle.dto.auth.RegisterResponse;
import com.group02.mindmingle.dto.user.UserDTO;
import com.group02.mindmingle.model.User;
import com.group02.mindmingle.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class AuthService {

        private final AuthenticationManager authenticationManager;
        private final UserService userService;
        private final JwtTokenUtil jwtTokenUtil;

        public AuthService(AuthenticationManager authenticationManager, UserService userService,
                        JwtTokenUtil jwtTokenUtil) {
                this.authenticationManager = authenticationManager;
                this.userService = userService;
                this.jwtTokenUtil = jwtTokenUtil;
        }

        /**
         * 优化的登录方法，使用UserDTO代替JwtResponse，并在这里处理cookie
         */
        public UserDTO login(LoginRequest loginRequest, HttpServletResponse response) {
                // 执行身份验证
                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                loginRequest.getEmail(),
                                                loginRequest.getPassword()));

                // 设置认证上下文
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 获取用户详情
                User userDetails = (User) authentication.getPrincipal();

                // 生成JWT令牌
                String jwt = jwtTokenUtil.generateToken(userDetails, userDetails.getId(), userDetails.getEmail());

                // 设置HTTP Only Cookie
                setAuthCookie(response, jwt);

                // 转换为DTO并返回
                return userService.convertToDto(userDetails);
        }

        /**
         * 设置认证Cookie
         */
        private void setAuthCookie(HttpServletResponse response, String token) {
                Cookie jwtCookie = new Cookie("jwt", token);
                jwtCookie.setHttpOnly(true);
                jwtCookie.setSecure(true); // 在生产环境中使用HTTPS时启用
                jwtCookie.setPath("/");
                jwtCookie.setMaxAge(86400); // 设置为与JWT令牌相同的过期时间

                response.addCookie(jwtCookie);
        }

        /**
         * 清除认证Cookie，用于登出
         */
        public void clearAuthCookie(HttpServletResponse response) {
                Cookie jwtCookie = new Cookie("jwt", null);
                jwtCookie.setHttpOnly(true);
                jwtCookie.setSecure(true);
                jwtCookie.setPath("/");
                jwtCookie.setMaxAge(0); // 立即过期

                response.addCookie(jwtCookie);
        }

        public RegisterResponse registerUser(RegisterRequest registerRequest) {
                // 构建用户实体
                User user = User.builder()
                                .username(registerRequest.getUsername())
                                .password(registerRequest.getPassword())
                                .email(registerRequest.getEmail())
                                .firstName(registerRequest.getFirstName())
                                .lastName(registerRequest.getLastName())
                                .roles(new HashSet<>())
                                .build();

                // 调用用户服务进行注册
                User registeredUser = userService.registerUser(user, false);

                // 返回注册响应
                return RegisterResponse.builder()
                                .message("用户注册成功")
                                .success(true)
                                .userId(registeredUser.getId())
                                .build();
        }
}
