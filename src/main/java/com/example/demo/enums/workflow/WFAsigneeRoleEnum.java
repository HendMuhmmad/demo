package com.example.demo.enums.workflow;

public enum WFAsigneeRoleEnum {
	 SUPER_ADMIN("Super Admin");
	
    private final String code;

    private WFAsigneeRoleEnum(String action) {
	this.code = action;
    }

    public String getCode() {
	return code;
    }

}
