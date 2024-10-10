package com.example.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WfProductDto {

    private int stockQuantity;
    private double price;
    private String productName;
    private String productId;
    private String color;
    private String description;
    private int productQuantity;
    private Long creatorId;
    private Long loginId;
    private Long status;

}
