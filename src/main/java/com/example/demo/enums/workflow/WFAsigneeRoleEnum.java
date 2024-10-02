package com.example.demo.enums.workflow;

public enum WFAsigneeRoleEnum {
    SUPERADMIN("Super Admin");

    private final String role;

    private WFAsigneeRoleEnum(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}