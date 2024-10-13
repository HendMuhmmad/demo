package com.example.demo.enums;
 
 

public enum WFAsigneeRoleEnum {

 
    SUPER_ADMIN("super admin");

    private final String code;

    private WFAsigneeRoleEnum(String action) {
	this.code = action;
    }

    public String getCode() {
	return code;
    }
}
