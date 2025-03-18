package com.group02.mindmingle.feature.user.service;

import com.group02.mindmingle.feature.auth.entity.Role;
import com.group02.mindmingle.feature.auth.repository.RoleRepository;
import com.group02.mindmingle.feature.user.entity.User;
import com.group02.mindmingle.feature.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 尝试通过用户名或邮箱查找用户
        return userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("用户名或邮箱不存在: " + username));
    }

    @Transactional
    public User registerUser(User user, boolean isAdmin) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已被占用");
        }

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
}
