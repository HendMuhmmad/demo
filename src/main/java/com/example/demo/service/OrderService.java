package com.example.demo.service;

import com.example.demo.model.dto.OrderResponseDto;

public interface OrderService {

    public OrderResponseDto getOrderDetails(String orderNumber);

}
