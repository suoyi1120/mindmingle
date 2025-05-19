package com.group02.mindmingle.service;

import com.group02.mindmingle.model.User;
import com.group02.mindmingle.repository.UserRepository;
import com.group02.mindmingle.repository.RoleRepository;
import com.group02.mindmingle.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private FileUploadService fileUploadService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_Success() {
        // Preparing test data
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");

        Role userRole = new Role();
        userRole.setName("USER");

        // Simulating dependent behaviors
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Executing the test
        User registeredUser = userService.registerUser(user, false);

        // Verify the results
        assertNotNull(registeredUser);
        assertEquals("testuser", registeredUser.getUsername());
        assertEquals("test@example.com", registeredUser.getEmail());
        assertEquals("encodedPassword", registeredUser.getPassword());

        // Verify method call
        verify(userRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("password123");
        verify(roleRepository).findByName("USER");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testLoadUserByEmail_Success() {
        // Preparing test data
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        // Simulating dependent behaviors
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Executing the test
        User foundUser = (User) userService.loadUserByEmail("test@example.com");

        // Verify the results
        assertNotNull(foundUser);
        assertEquals("testuser", foundUser.getUsername());
        assertEquals("test@example.com", foundUser.getEmail());

        // Verify method call
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void testLoadUserByEmail_NotFound() {
        // Simulating dependent behaviors
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Execute the test and verify the exception
        assertThrows(RuntimeException.class, () -> {
            userService.loadUserByEmail("nonexistent@example.com");
        });

        // Verify method call
        verify(userRepository).findByEmail("nonexistent@example.com");
    }
} 