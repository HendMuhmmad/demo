package com.example.demo.model.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private int stockQuantity;
    private double price;
    private String productName;
    private String color;
    private String description;
    private int productQuantity;
    private Long creatorId;
    private Date creationDate;
    private Long loginId;

}
