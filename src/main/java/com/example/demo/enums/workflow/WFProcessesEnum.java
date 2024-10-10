package com.example.demo.enums.workflow;

public enum WFProcessesEnum {
    ADD_PRODUCT(1),
    UPDATE_PRODUCT(2),
    DELETE_PRODUCT(3);

    private Long code;

    private WFProcessesEnum(long code) {
	this.code = code;
    }

    public long getCode() {
	return code;
    }
}