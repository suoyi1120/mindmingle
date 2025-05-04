package com.group02.mindmingle.dto.gemini;

import java.util.List;

public class Prompt {
    private List<Contents> contents;

    public Prompt() {
    }

    public List<Contents> getContents() {
        return contents;
    }

    public void setContents(List<Contents> contents) {
        this.contents = contents;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Prompt{\n");
        if (contents != null) {
            sb.append("  contents: [\n");
            for (int i = 0; i < contents.size(); i++) {
                Contents content = contents.get(i);
                sb.append("    {\n");
                sb.append("      role: ").append(content.getRole()).append(",\n");
                if (content.getParts() != null) {
                    sb.append("      parts: [\n");
                    for (int j = 0; j < content.getParts().size(); j++) {
                        Parts part = content.getParts().get(j);
                        sb.append("        {\n");
                        if (part.getText() != null) {
                            // 限制文本长度，防止输出过长
                            String text = part.getText();
                            sb.append("          text: ").append(text).append("\n");
                        }
                        sb.append("        }");
                        if (j < content.getParts().size() - 1) {
                            sb.append(",");
                        }
                        sb.append("\n");
                    }
                    sb.append("      ]\n");
                }
                sb.append("    }");
                if (i < contents.size() - 1) {
                    sb.append(",");
                }
                sb.append("\n");
            }
            sb.append("  ]\n");
        }
        sb.append("}");
        return sb.toString();
    }
}