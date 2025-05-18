package com.group02.mindmingle.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private List<String> roles;
    private LocalDateTime createdAt;

    // 用户配置相关字段
    private String backgroundColor;
    private String cardColor;
    private String avatarType;
    private String avatarEmoji;
    private String avatarUrl;
}
