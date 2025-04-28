package com.group02.mindmingle.dto.gemini;

import java.util.List;

public class Contents {
    private List<Parts> parts;
    private String role;

    public Contents() {
    }

    public List<Parts> getParts() {
        return parts;
    }

    public void setParts(List<Parts> parts) {
        this.parts = parts;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}