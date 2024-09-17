package com.example.demo.model.dto;

import java.util.Date;

import lombok.Data;

@Data
public class ProductDto {

	private int quantity;
    private double price;
    private String productName;
    private String color;
    private String description;
    private int productQuantity;
    private int creatorId;
    private Date creationDate;
    private int loginId;

}
