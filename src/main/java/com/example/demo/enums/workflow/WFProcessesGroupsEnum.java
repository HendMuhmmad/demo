package com.example.demo.enums.workflow;

public enum WFProcessesGroupsEnum {
    PRODUCT(1);

    private long code;

    private WFProcessesGroupsEnum(long code) {
	this.code = code;
    }

    public long getCode() {
	return code;
    }
}
