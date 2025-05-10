package com.group02.mindmingle.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    // 自定义配置字段
    private String backgroundColor;
    private String cardColor;
    private String nickname;

    // 新增：头像相关字段
    private String avatarType;   // 'emoji' | 'upload' | 'default'
    private String avatarEmoji;  // 如：'🦄'
    private String avatarUrl;    // 如果是上传头像，则为 base64 或链接
}
