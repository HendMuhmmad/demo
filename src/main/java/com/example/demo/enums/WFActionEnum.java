 
package com.example.demo.enums;

public enum WFActionEnum {

    APPROVED(1L),
    REJECTED(2L);
    private final Long code;

    private WFActionEnum(Long id) {
	this.code = id;
    }

    public Long getCode() {
	return code;
    }
}
