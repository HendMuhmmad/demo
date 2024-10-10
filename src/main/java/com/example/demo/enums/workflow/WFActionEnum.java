package com.example.demo.enums.workflow;

public enum WFActionEnum {
	    APPROVE(1L),
	    REJECT(2L);
	    private Long code;

	    private WFActionEnum(Long code) {
		this.code = code;
	    }

	    public Long getCode() {
		return code;
	    }
}
