package com.example.demo.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
	private Long id;
    private int stockQuantity;
    private double price;
    private String productName;
    private String color;
    private String description;
    private Long loginId;
    private int productQuantity;

}
