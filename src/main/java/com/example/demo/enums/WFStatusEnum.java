package com.example.demo.enums;
 
public enum WFStatusEnum {
	
    UNDER_APPROVAL(0L),
    APPROVED(1L),
    REJECTED(2L);
	
    private final long code;
 
    private WFStatusEnum(long status) {
	this.code = status;
    }
 
    public long getCode() {
	return code;
    }
 
}