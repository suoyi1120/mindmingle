package com.group02.mindmingle.service;

import com.group02.mindmingle.model.User;
import com.group02.mindmingle.repository.UserRepository;
import com.group02.mindmingle.common.security.JwtTokenUtil;
import com.group02.mindmingle.dto.auth.LoginRequest;
import com.group02.mindmingle.dto.user.UserDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLogin_Success() {
        // Preparing test data
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");
        
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setId(1L);

        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");

        // Simulating dependent behaviors
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(jwtTokenUtil.generateToken(any(User.class), anyLong(), anyString()))
            .thenReturn("jwt-token");
        when(userService.convertToDto(any(User.class))).thenReturn(userDTO);

        // Executing the test
        UserDTO result = authService.login(loginRequest, response);

        // Verify the results
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());

        // Verify method call
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenUtil).generateToken(eq(user), eq(user.getId()), eq(user.getEmail()));
        verify(userService).convertToDto(user);
    }

    @Test
    void testLogin_InvalidCredentials() {
        // Preparing test data
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("wrongpassword");

        // Simulating dependent behaviors
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new RuntimeException("Invalid credentials"));

        // Execute the test and verify the exception
        assertThrows(RuntimeException.class, () -> {
            authService.login(loginRequest, response);
        });

        // Verify method call
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenUtil, never()).generateToken(any(User.class), anyLong(), anyString());
        verify(userService, never()).convertToDto(any(User.class));
    }
} 