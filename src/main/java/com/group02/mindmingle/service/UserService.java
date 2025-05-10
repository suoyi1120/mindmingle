package com.group02.mindmingle.service;

import com.group02.mindmingle.dto.user.UserDTO;
import com.group02.mindmingle.model.ProfileData;
import com.group02.mindmingle.model.Role;
import com.group02.mindmingle.model.User;
import com.group02.mindmingle.repository.RoleRepository;
import com.group02.mindmingle.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository,
                       @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + email));
    }

    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + email));
    }

    @Transactional
    public User registerUser(User user, boolean isAdmin) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("邮箱已被注册");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

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

    public UserDTO convertToDto(User user) {
        if (user == null) return null;

        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        ProfileData profile = user.getProfileData();

        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(roles)
                .nickname(profile != null ? profile.getNickname() : null)
                .backgroundColor(profile != null ? profile.getBackgroundColor() : null)
                .cardColor(profile != null ? profile.getCardColor() : null)
                .avatarType(profile != null ? profile.getAvatarType() : null)
                .avatarEmoji(profile != null ? profile.getAvatarEmoji() : null)
                .avatarUrl(profile != null ? profile.getAvatarUrl() : null)
                .build();
    }

    public UserDTO getCurrentUserDto(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + email));
        return convertToDto(user);
    }

    @Transactional
    public void save(User user) {
        // Ensure we're flushing changes to the database
        userRepository.saveAndFlush(user);
    }
}