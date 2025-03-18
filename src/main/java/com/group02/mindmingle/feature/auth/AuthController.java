package com.group02.mindmingle.feature.auth;

import com.group02.mindmingle.common.security.JwtTokenUtil;
import com.group02.mindmingle.feature.auth.dto.JwtResponse;
import com.group02.mindmingle.feature.auth.dto.LoginRequest;
import com.group02.mindmingle.feature.auth.dto.RegisterRequest;
import com.group02.mindmingle.feature.user.User;
import com.group02.mindmingle.feature.user.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

        private final AuthenticationManager authenticationManager;
        private final UserService userService;
        private final JwtTokenUtil jwtTokenUtil;

        public AuthController(AuthenticationManager authenticationManager, UserService userService,
                        JwtTokenUtil jwtTokenUtil) {
                this.authenticationManager = authenticationManager;
                this.userService = userService;
                this.jwtTokenUtil = jwtTokenUtil;
        }

        @PostMapping("/login")
        public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
                System.out.println(loginRequest);
                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                loginRequest.getEmail(),
                                                loginRequest.getPassword()));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                String jwt = jwtTokenUtil.generateToken((User) authentication.getPrincipal());

                User userDetails = (User) authentication.getPrincipal();
                List<String> roles = userDetails.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList());

                return ResponseEntity.ok(JwtResponse.builder()
                                .token(jwt)
                                .id(userDetails.getId())
                                .username(userDetails.getUsername())
                                .email(userDetails.getEmail())
                                .roles(roles)
                                .build());
        }

        @PostMapping("/register")
        public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
                System.out.println(registerRequest);
                User user = User.builder()
                                .username(registerRequest.getUsername())
                                .password(registerRequest.getPassword())
                                .email(registerRequest.getEmail())
                                .firstName(registerRequest.getFirstName())
                                .lastName(registerRequest.getLastName())
                                .roles(new HashSet<>())
                                .build();

                userService.registerUser(user, false);

                return ResponseEntity.ok("用户注册成功");
        }
}
