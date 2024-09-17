package com.example.demo.model.dto;

import java.util.Date;

import lombok.Data;

@Data
public class ProductDto {
    private int id;
    private int quantity;
    private double price;
    private int productId;
    private String productName;
    private String color;
    private String description;
    private double actualPrice;
    private int productQuantity;
    private int creatorId;
    private Date creationDate;
    private int loginId;

}
