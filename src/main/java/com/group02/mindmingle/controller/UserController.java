package com.group02.mindmingle.controller;

import com.group02.mindmingle.dto.user.UserDTO;
import com.group02.mindmingle.dto.user.UserProfileUpdateRequest;
import com.group02.mindmingle.model.User;
import com.group02.mindmingle.service.FileUploadService;
import com.group02.mindmingle.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final FileUploadService fileUploadService;

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

    /**
     * 更新用户名
     */
    @PutMapping("/user/profile/username")
    public ResponseEntity<UserDTO> updateUsername(Authentication authentication,
            @RequestBody Map<String, String> payload) {
        User user = (User) authentication.getPrincipal();
        String newUsername = payload.get("username");
        UserDTO updatedUser = userService.updateUsername(user.getId(), newUsername);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * 更新背景颜色
     */
    @PutMapping("/user/profile/background-color")
    public ResponseEntity<UserDTO> updateBackgroundColor(Authentication authentication,
            @RequestBody Map<String, String> payload) {
        User user = (User) authentication.getPrincipal();
        String backgroundColor = payload.get("backgroundColor");
        UserDTO updatedUser = userService.updateBackgroundColor(user.getId(), backgroundColor);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * 更新卡片颜色
     */
    @PutMapping("/user/profile/card-color")
    public ResponseEntity<UserDTO> updateCardColor(Authentication authentication,
            @RequestBody Map<String, String> payload) {
        User user = (User) authentication.getPrincipal();
        String cardColor = payload.get("cardColor");
        UserDTO updatedUser = userService.updateCardColor(user.getId(), cardColor);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * 更新Emoji头像
     */
    @PutMapping("/user/profile/emoji-avatar")
    public ResponseEntity<UserDTO> updateEmojiAvatar(Authentication authentication,
            @RequestBody Map<String, String> payload) {
        User user = (User) authentication.getPrincipal();
        String emoji = payload.get("emoji");
        UserDTO updatedUser = userService.updateEmojiAvatar(user.getId(), emoji);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * 上传头像文件
     */
    @PostMapping("/user/profile/avatar")
    public ResponseEntity<UserDTO> uploadAvatar(Authentication authentication,
            @RequestParam("file") MultipartFile file) {
        try {
            User user = (User) authentication.getPrincipal();

            // 先上传文件到blob存储
            String uploadedFileUrl = fileUploadService.uploadFile(file, "user_avatars");
            log.info("头像文件上传成功，URL: {}", uploadedFileUrl);

            // 然后更新用户头像URL
            UserDTO updatedUser = userService.updateAvatarUrl(user.getId(), uploadedFileUrl);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            log.error("上传头像失败", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 更新用户配置（一次更新多个属性）
     */
    @PutMapping("/user/profile/settings")
    public ResponseEntity<UserDTO> updateUserProfile(Authentication authentication,
            @RequestBody UserProfileUpdateRequest request) {
        User user = (User) authentication.getPrincipal();
        UserDTO updatedUser = userService.updateUserProfile(user.getId(), request);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminEndpoint() {
        return ResponseEntity.ok("这是一个管理员端点，只有管理员可以访问");
    }

    @PostMapping("/user/rewards")
    public ResponseEntity<String> addReward(@RequestParam long rewardId) {
        userService.addReward(rewardId);
        return ResponseEntity.ok("Reward added successfully.");
    }
}
