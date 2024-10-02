package com.example.demo.enums.workflow;


public enum WFActionEnum {
    APPROVED(1L),
    REJECTED(2L);

    private final Long action;

    WFActionEnum(Long action) {
        this.action = action;
    }

    public Long getAction() {
        return action;
    }
}
