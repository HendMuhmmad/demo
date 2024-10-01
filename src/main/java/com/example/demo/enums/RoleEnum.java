package com.example.demo.enums;

public enum RoleEnum {

    HEAD_OF_DEPARTMENT(1L),
    SUPER_ADMIN(2L),
    ADMIN(3L),
    CUSTOMER(4L);

    private final Long code;

    private RoleEnum(Long roleId) {
	this.code = roleId;
    }

    public Long getCode() {
	return code;
    }
}
