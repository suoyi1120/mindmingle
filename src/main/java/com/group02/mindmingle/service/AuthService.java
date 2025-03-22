package com.group02.mindmingle.service;

import com.group02.mindmingle.common.security.JwtTokenUtil;
import com.group02.mindmingle.dto.auth.JwtResponse;
import com.group02.mindmingle.dto.auth.LoginRequest;
import com.group02.mindmingle.dto.auth.RegisterRequest;
import com.group02.mindmingle.dto.auth.RegisterResponse;
import com.group02.mindmingle.model.User;
import com.group02.mindmingle.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

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

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        // 执行身份验证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()));

        // 设置认证上下文
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 生成JWT令牌
        User userDetails = (User) authentication.getPrincipal();
        System.out.println("userDetails: " + userDetails);
        String jwt = jwtTokenUtil.generateToken(userDetails);

        // 提取角色信息
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // 构建并返回响应
        return JwtResponse.builder()
                .token(jwt)
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .roles(roles)
                .build();
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
