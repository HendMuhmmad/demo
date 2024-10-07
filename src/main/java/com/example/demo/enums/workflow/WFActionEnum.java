package com.example.demo.enums.workflow;

public enum WFActionEnum {
	
    APPROVED(1L),
    REJECTED(2L),
    RUNNING(3L);
	
    private final long code;

    private WFActionEnum(long action) {
	this.code = action;
    }

    public long getCode() {
	return code;
    }

}
