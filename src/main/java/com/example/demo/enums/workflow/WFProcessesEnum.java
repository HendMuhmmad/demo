package com.example.demo.enums.workflow;

public enum WFProcessesEnum {
    ADD_PRODUCT(1L),
    UPDATE_PRODUCT(2L),
    DELETE_PRODUCT(3L);

    private Long code;

    private WFProcessesEnum(Long code) {
	this.code = code;
    }

    public Long getCode() {
	return code;
    }
}