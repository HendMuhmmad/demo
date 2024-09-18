package com.example.demo.model.dto;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Setter
@AllArgsConstructor
public class OrderResponseDto {
    
    private int orderId;
    private int userId;
    private double totalPrice;
    private Date transactionDate;
    private String orderNumber;
    private CustomerDto customer;
    private List<ProductDto> items;
}
