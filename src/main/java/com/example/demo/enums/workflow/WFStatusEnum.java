package com.example.demo.enums.workflow;

public enum WFStatusEnum {
	UNDERAPPROVAL(0L),
	APPROVED(1L),
	REJECTED(2L);
	private Long code;

	public Long getCode() {
		return code;
	}

	private WFStatusEnum(Long code) {
		this.code = code;
	}
	
	
}
