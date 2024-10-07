package com.example.demo.enums.workflow;

public enum WFStatusEnum {
	
    UNDER_APPROVAL(0),
    APPROVED(1),
    REJECTED(2);
	
    private final long code;

    private WFStatusEnum(long status) {
	this.code = status;
    }

    public long getCode() {
	return code;
    }

}
