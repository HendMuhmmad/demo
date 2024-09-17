package com.example.demo.model.dto;

import lombok.Data;

@Data
public class ProductUpdateStockQuantityDTO {
	
	private int id;
	private int stockQuantity;
	private int loginId;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getStockQuantity() {
		return stockQuantity;
	}
	public void setStockQuantity(int stockQuantity) {
		this.stockQuantity = stockQuantity;
	}
	public int getLoginId() {
		return loginId;
	}
	public void setLoginId(int loginId) {
		this.loginId = loginId;
	}
	
	

}
