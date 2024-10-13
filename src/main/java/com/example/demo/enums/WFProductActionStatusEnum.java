 

package com.example.demo.enums;

public enum WFProductActionStatusEnum {
	
    ADD(0L),
    UPDATE(1L),
    DELETE(2L);
	
    private final long code;
 
    private WFProductActionStatusEnum(long status) {
	this.code = status;
    }
 
    public long getCode() {
	return code;
    }
 
}