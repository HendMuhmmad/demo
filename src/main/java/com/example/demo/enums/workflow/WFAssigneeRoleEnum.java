package com.example.demo.enums.workflow;

public enum WFAssigneeRoleEnum {
    SUPERADMIN("Super Admin");

    private final String role;

    private WFAssigneeRoleEnum(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}