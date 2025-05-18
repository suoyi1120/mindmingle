package com.group02.mindmingle.service;

import com.group02.mindmingle.dto.user.UserDTO;
import com.group02.mindmingle.model.Reward;
import com.group02.mindmingle.dto.user.UserProfileUpdateRequest;
import com.group02.mindmingle.exception.ResourceNotFoundException;
import com.group02.mindmingle.model.Role;
import com.group02.mindmingle.repository.RewardRepository;
import com.group02.mindmingle.repository.RoleRepository;
import com.group02.mindmingle.model.User;
import com.group02.mindmingle.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileUploadService fileUploadService;

    @Autowired
    private RewardRepository rewardRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository,
            @Lazy PasswordEncoder passwordEncoder, FileUploadService fileUploadService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileUploadService = fileUploadService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 只通过邮箱查找用户
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + email));
    }

    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + email));
    }

    @Transactional
    public User registerUser(User user, boolean isAdmin) {
        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("邮箱已被注册");
        }

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 分配角色
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("角色不存在"));
        user.getRoles().add(userRole);

        if (isAdmin) {
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("角色不存在"));
            user.getRoles().add(adminRole);
        }

        return userRepository.save(user);
    }

    /**
     * 将User实体转换为UserDTO
     */
    public UserDTO convertToDto(User user) {
        if (user == null) {
            return null;
        }

        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(roles)
                .backgroundColor(user.getBackgroundColor())
                .cardColor(user.getCardColor())
                .avatarType(user.getAvatarType())
                .avatarEmoji(user.getAvatarEmoji())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }

    /**
     * 获取当前用户的DTO
     */
    public UserDTO getCurrentUserDto(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + email));
        return convertToDto(user);
    }

    @Transactional
    public void addReward(long rewardId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByEmail(currentUser.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));

        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new RuntimeException("奖励不存在: " + rewardId));

        user.getRewards().add(reward);  // 多对多关系自动建立
        userRepository.save(user);
    }

    /**
     * 更新用户名
     */
    @Transactional
    public UserDTO updateUsername(Long userId, String newUsername) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在: " + userId));

        // 检查用户名是否已存在
        if (userRepository.existsByUsername(newUsername) && !user.getUsername().equals(newUsername)) {
            throw new RuntimeException("用户名已被使用");
        }

        user.setUsername(newUsername);
        User updatedUser = userRepository.save(user);
        return convertToDto(updatedUser);
    }

    /**
     * 更新背景颜色
     */
    @Transactional
    public UserDTO updateBackgroundColor(Long userId, String backgroundColor) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在: " + userId));

        user.setBackgroundColor(backgroundColor);
        User updatedUser = userRepository.save(user);
        return convertToDto(updatedUser);
    }

    /**
     * 更新卡片颜色
     */
    @Transactional
    public UserDTO updateCardColor(Long userId, String cardColor) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在: " + userId));

        user.setCardColor(cardColor);
        User updatedUser = userRepository.save(user);
        return convertToDto(updatedUser);
    }

    /**
     * 更新Emoji头像
     */
    @Transactional
    public UserDTO updateEmojiAvatar(Long userId, String emoji) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在: " + userId));

        user.setAvatarType("emoji");
        user.setAvatarEmoji(emoji);
        User updatedUser = userRepository.save(user);
        return convertToDto(updatedUser);
    }

    /**
     * 更新上传的头像
     */
    @Transactional
    public UserDTO updateAvatarUrl(Long userId, String avatarUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在: " + userId));

        // 保存旧的头像URL，用于后续删除
        String oldAvatarUrl = user.getAvatarUrl();

        // 更新头像URL和类型
        user.setAvatarUrl(avatarUrl);
        user.setAvatarType("upload");

        // 保存更新后的用户信息
        User updatedUser = userRepository.save(user);

        // 如果旧头像URL存在且不是新提供的头像URL，则删除旧头像
        if (oldAvatarUrl != null && !oldAvatarUrl.isEmpty()
                && !oldAvatarUrl.equals(avatarUrl)) {
            try {
                log.info("删除旧的用户头像: {}", oldAvatarUrl);
                fileUploadService.deleteFile(oldAvatarUrl);
            } catch (Exception e) {
                // 记录错误但不影响主流程
                log.error("删除旧的用户头像时出错: {}", e.getMessage(), e);
            }
        }

        return convertToDto(updatedUser);
    }

    /**
     * 更新用户配置
     */
    @Transactional
    public UserDTO updateUserProfile(Long userId, UserProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在: " + userId));

        // 更新用户名（如果提供）
        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            // 检查用户名是否已存在
            if (userRepository.existsByUsername(request.getUsername())
                    && !user.getUsername().equals(request.getUsername())) {
                throw new RuntimeException("用户名已被使用");
            }
            user.setUsername(request.getUsername());
        }

        // 更新背景颜色（如果提供）
        if (request.getBackgroundColor() != null) {
            user.setBackgroundColor(request.getBackgroundColor());
        }

        // 更新卡片颜色（如果提供）
        if (request.getCardColor() != null) {
            user.setCardColor(request.getCardColor());
        }

        // 更新头像相关信息（如果提供）
        if (request.getAvatarType() != null) {
            user.setAvatarType(request.getAvatarType());

            if ("emoji".equals(request.getAvatarType()) && request.getAvatarEmoji() != null) {
                user.setAvatarEmoji(request.getAvatarEmoji());
            } else if ("upload".equals(request.getAvatarType()) && request.getAvatarUrl() != null) {
                // 保存旧的头像URL，用于后续删除
                String oldAvatarUrl = user.getAvatarUrl();

                // 更新头像URL
                user.setAvatarUrl(request.getAvatarUrl());

                // 如果旧头像URL存在且不是新提供的头像URL，则删除旧头像
                if (oldAvatarUrl != null && !oldAvatarUrl.isEmpty()
                        && !oldAvatarUrl.equals(request.getAvatarUrl())) {
                    try {
                        log.info("删除旧的用户头像: {}", oldAvatarUrl);
                        fileUploadService.deleteFile(oldAvatarUrl);
                    } catch (Exception e) {
                        // 记录错误但不影响主流程
                        log.error("删除旧的用户头像时出错: {}", e.getMessage(), e);
                    }
                }
            }
        }

        User updatedUser = userRepository.save(user);
        return convertToDto(updatedUser);
    }
}
