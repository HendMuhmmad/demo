package com.example.demo.enums.workflow;

public enum WFProductEnum {
	
    ADDED(1),
    DELETED(2),
    UPDATED(3);
	
    private final int code;

    private WFProductEnum(int action) {
	this.code = action;
    }

    public int getCode() {
	return code;
    }

}
