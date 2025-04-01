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
}