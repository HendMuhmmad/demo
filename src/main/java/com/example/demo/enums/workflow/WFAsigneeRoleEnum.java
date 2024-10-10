package com.example.demo.enums.workflow;

public enum WFAsigneeRoleEnum {
	PRODUCT_MANAGER("PRODUCT_MANAGER");

	private String code;

	private WFAsigneeRoleEnum(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

}
