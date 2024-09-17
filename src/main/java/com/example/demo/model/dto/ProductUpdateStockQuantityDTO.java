package com.example.demo.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductUpdateStockQuantityDTO {

    private int id;
    private int stockQuantity;
    private int loginId;

}
