package com.group02.mindmingle.controller;

import com.group02.mindmingle.dto.user.UserDTO;
import com.group02.mindmingle.model.ProfileData;
import com.group02.mindmingle.model.User;
import com.group02.mindmingle.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/user/profile/settings")
    public ResponseEntity<?> updateUserSettings(@RequestBody ProfileData profileData, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();

            // Create a new ProfileData instance to ensure change detection
            ProfileData newProfileData = new ProfileData();

            // Copy all fields explicitly
            newProfileData.setBackgroundColor(profileData.getBackgroundColor());
            newProfileData.setCardColor(profileData.getCardColor());
            newProfileData.setNickname(profileData.getNickname());
            newProfileData.setAvatarType(profileData.getAvatarType());
            newProfileData.setAvatarEmoji(profileData.getAvatarEmoji());
            newProfileData.setAvatarUrl(profileData.getAvatarUrl());

            // Set the new profile data
            user.setProfileData(newProfileData);

            // Save the user
            userService.save(user);

            return ResponseEntity.ok("设置已保存");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("无法保存设置: " + e.getMessage());
        }
    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminEndpoint() {
        return ResponseEntity.ok("这是一个管理员端点，只有管理员可以访问");
    }
}