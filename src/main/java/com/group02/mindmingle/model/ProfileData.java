package com.group02.mindmingle.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileData {
    private String backgroundColor;
    private String cardColor;
    private String nickname;
    private String avatarType;   // 'emoji' | 'upload' | 'default'
    private String avatarEmoji;  // 如果是 emoji 类型，记录 emoji 符号
    private String avatarUrl;    // 如果是上传头像，记录 base64 或 URL
}

