package com.group02.mindmingle.dto.gemini; // 假设放在 dto 包下

import java.util.ArrayList;
import java.util.List;

public class ChatRequest {

    private String prompt;
    private String systemPrompt;

    // Jackson (Spring 默认的 JSON 库) 需要一个无参构造函数
    public ChatRequest() {
    }

    // Getter - Jackson 需要 getter 来访问字段值
    public String getPrompt() {
        return prompt;
    }

    // Setter - Jackson 需要 setter 来设置字段值
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }

    // 辅助方法，将ChatRequest转换为Prompt对象
    public Prompt toPrompt() {
        Prompt promptObj = new Prompt();
        List<Contents> contentsList = new ArrayList<>();

        // 添加系统提示作为第一个用户消息
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            Contents systemContents = new Contents();
            systemContents.setRole("user");

            List<Parts> systemPartsList = new ArrayList<>();
            Parts systemPart = new Parts();
            systemPart.setText(systemPrompt);
            systemPartsList.add(systemPart);

            systemContents.setParts(systemPartsList);
            contentsList.add(systemContents);
        }

        // 添加用户实际提示作为第二个用户消息
        Contents userContents = new Contents();
        userContents.setRole("user");

        List<Parts> userPartsList = new ArrayList<>();
        Parts userPart = new Parts();
        userPart.setText(this.prompt);
        userPartsList.add(userPart);

        userContents.setParts(userPartsList);
        contentsList.add(userContents);

        promptObj.setContents(contentsList);
        return promptObj;
    }

    // (可选) 如果你使用了 Lombok 依赖，可以用 @Data 或 @Getter/@Setter 简化
    // import lombok.Data;
    // @Data
    // public class ChatRequest {
    // private String prompt;
    // }
}