package com.group02.mindmingle.dto.gemini;

import java.util.List;

public class Contents {
    private List<Parts> parts;

    public Contents() {
    }

    public List<Parts> getParts() {
        return parts;
    }

    public void setParts(List<Parts> parts) {
        this.parts = parts;
    }
}