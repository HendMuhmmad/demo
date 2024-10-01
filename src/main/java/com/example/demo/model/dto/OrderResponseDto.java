package com.example.demo.model.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class OrderResponseDto {
    
    private Long orderId;
    private Long userId;
    private double totalPrice;
    private Date transactionDate;
    private String orderNumber;
    private CustomerDto customer;
    private List<ProductDto> items;
}
