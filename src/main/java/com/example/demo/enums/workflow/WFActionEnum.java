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
    public static Long fromString(String Action) {
    	if (Action.equalsIgnoreCase("approve")||Action.equalsIgnoreCase("approved")) return APPROVED.getAction();
    	if (Action.equalsIgnoreCase("reject")||Action.equalsIgnoreCase("rejected")) return REJECTED.getAction();
        
    	throw new IllegalArgumentException("Unknown action: " + Action);
    }
}
