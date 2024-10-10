package com.example.demo.enums.workflow;

public enum SuperAdminApprovel {
    SUPER_ADMIN(1);

    private long code;

    private SuperAdminApprovel(long code) {
	this.code = code;
    }

    public long getCode() {
	return code;
    }

}
