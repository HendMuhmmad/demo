package com.example.demo.enums;

public enum WFActionEnum {
    APPROVE(1),
    REJECTED(2);

    private long code;

    private WFActionEnum(long code) {
	this.code = code;
    }

    public long getCode() {
	return code;
    }

}
