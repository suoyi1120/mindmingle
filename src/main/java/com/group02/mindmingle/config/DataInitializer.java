package com.group02.mindmingle.config;

import com.group02.mindmingle.feature.auth.entity.Role;
import com.group02.mindmingle.feature.auth.repository.RoleRepository;
import com.group02.mindmingle.feature.user.entity.User;
import com.group02.mindmingle.feature.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository,
            @Lazy PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 初始化角色
        initRoles();

        // 初始化管理员账户
        initAdminUser();
    }

    private void initRoles() {
        log.info("开始初始化角色...");

        if (roleRepository.count() == 0) {
            Role userRole = Role.builder()
                    .name("USER")
                    .description("普通用户角色")
                    .build();

            Role adminRole = Role.builder()
                    .name("ADMIN")
                    .description("管理员角色")
                    .build();

            roleRepository.save(userRole);
            roleRepository.save(adminRole);

            log.info("角色初始化完成");
        } else {
            log.info("角色已存在，跳过初始化");
        }
    }

    private void initAdminUser() {
        log.info("开始初始化管理员账户...");

        if (!userRepository.existsByUsername("admin")) {
            Set<Role> roles = new HashSet<>();

            Role userRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new RuntimeException("角色不存在"));
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("角色不存在"));

            roles.add(userRole);
            roles.add(adminRole);

            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("Admin123"))
                    .email("admin@mindmingle.com")
                    .firstName("Admin")
                    .lastName("User")
                    .roles(roles)
                    .build();

            userRepository.save(admin);

            log.info("管理员账户初始化完成");
        } else {
            log.info("管理员账户已存在，跳过初始化");
        }
    }
}
