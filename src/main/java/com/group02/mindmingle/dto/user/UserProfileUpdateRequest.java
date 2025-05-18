package com.group02.mindmingle.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileUpdateRequest {
    private String username;
    private String backgroundColor;
    private String cardColor;
    private String avatarType;
    private String avatarEmoji;
    private String avatarUrl;
}