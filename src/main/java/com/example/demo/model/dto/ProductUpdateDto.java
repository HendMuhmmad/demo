package com.example.demo.model.dto;

import java.util.Date;

import lombok.Data;

@Data
public class ProductUpdateDto {
	private int id;
    private int stockQuantity;
    private double price;
    private String productName;
    private String color;
    private String description;
    private Date creationDate;
    private int loginId;

}
