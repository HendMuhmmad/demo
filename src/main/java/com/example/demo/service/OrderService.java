package com.example.demo.service;

import java.util.List;

import com.example.demo.model.dto.OrderResponseDto;
import com.example.demo.model.dto.order.OrderDTO;
import com.example.demo.model.orm.OrderDetails;

public interface OrderService {
    OrderDTO createOrder(int userId,List<OrderDetails> orderDetails);
    public OrderResponseDto getOrderDetails(String orderNumber);
}
