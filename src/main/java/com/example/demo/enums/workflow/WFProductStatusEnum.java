package com.example.demo.enums.workflow;

public enum WFProductStatusEnum {
    ADD(1),
    UPDATE(2),
    DELETE(3);

    private long code;

    private WFProductStatusEnum(long code) {
	this.code = code;
    }

    public long getCode() {
	return code;
    }
}
