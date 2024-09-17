package com.example.demo.service;

import java.util.List;

import com.example.demo.model.dto.OrderResponseDto;
import com.example.demo.model.orm.Vw_Order_Details;

public interface OrderService {
	
    public OrderResponseDto getOrderDetails(String orderNumber);

}
