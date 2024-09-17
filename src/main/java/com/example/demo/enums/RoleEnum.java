package com.example.demo.enums;

public enum RoleEnum {

    HEAD_OF_DEPARTMENT(1),
    SUPER_ADMIN(2),
    ADMIN(3),
    CUSTOMER(4);

    private final int code;

    private RoleEnum(int roleId) {
	this.code = roleId;
    }

    public int getCode() {
	return code;
    }
}
