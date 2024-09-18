package com.example.demo.model.dto;

import lombok.Data;

@Data
public class ProductDto {
    private String productName;
    private String description;
    private String color;
    private int stockQuantity;
    private double price;
    private int orderedQuantity;
    private int loginId;

}