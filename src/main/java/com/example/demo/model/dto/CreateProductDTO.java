package com.example.demo.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateProductDTO {

    private String productName;
    private double price;
    private String color;
    private int stockQuantity;
    private String description;
    private int loginId;

}
