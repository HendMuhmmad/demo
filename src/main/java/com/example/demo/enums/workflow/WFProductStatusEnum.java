package com.example.demo.enums.workflow;

public enum WFProductStatusEnum {
	ADDED(1L),
	UPDATED(2L),
	DELETED(3L);
	private Long code;

	public Long getCode() {
		return code;
	}

	private WFProductStatusEnum(Long code) {
		this.code = code;
	}
    
	public static WFProductStatusEnum fromCode(Long code) {
        for (WFProductStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No status found for code: " + code);
    }
	
	
}
