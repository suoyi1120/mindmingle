package com.group02.mindmingle.dto.gemini; // 假设放在 dto 包下

import java.util.ArrayList;
import java.util.List;

public class ChatRequest {

    private String prompt;

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

    // 辅助方法，将ChatRequest转换为Prompt对象
    public Prompt toPrompt() {
        Prompt prompt = new Prompt();
        Contents contents = new Contents();
        Parts part = new Parts();

        part.setText(this.prompt);

        List<Parts> partsList = new ArrayList<>();
        partsList.add(part);

        contents.setParts(partsList);

        List<Contents> contentsList = new ArrayList<>();
        contentsList.add(contents);

        prompt.setContents(contentsList);

        return prompt;
    }

    // (可选) 如果你使用了 Lombok 依赖，可以用 @Data 或 @Getter/@Setter 简化
    // import lombok.Data;
    // @Data
    // public class ChatRequest {
    // private String prompt;
    // }
}