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
    public static Long fromString(String Action) {
    	if (Action.equalsIgnoreCase("approve")||Action.equalsIgnoreCase("approved")) return APPROVED.getCode();
    	if (Action.equalsIgnoreCase("reject")||Action.equalsIgnoreCase("rejected")) return REJECTED.getCode();
    	if (Action.equalsIgnoreCase("underapproval")) return UNDERAPPROVAL.getCode();
    	throw new IllegalArgumentException("Unknown action: " + Action);
    }
	
	
}
