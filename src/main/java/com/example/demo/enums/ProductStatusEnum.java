package com.example.demo.enums;

public enum ProductStatusEnum {
	ADD(1), UPDATE(2), DELETE(3);

	private final Integer code;

	private ProductStatusEnum(Integer code) {
		this.code = code;
	}

	public Integer getCode() {
		return code;
	}
}
