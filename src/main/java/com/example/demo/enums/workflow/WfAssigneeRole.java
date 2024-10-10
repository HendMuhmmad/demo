package com.example.demo.enums.workflow;

public enum WfAssigneeRole {
    SUPER_ADMIN("Super Admin");

    private String code;

    private WfAssigneeRole(String code) {
	this.code = code;
    }

    public String getCode() {
	return code;
    }
}
