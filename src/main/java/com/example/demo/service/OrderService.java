package com.example.demo.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.example.demo.model.dto.OrderResponseDto;
import com.example.demo.model.dto.order.OrderDTO;
import com.example.demo.model.orm.OrderDetails;

public interface OrderService {
    OrderDTO createOrder(int userId, List<OrderDetails> orderDetails);

    public ResponseEntity<OrderResponseDto> getOrderDetailsByOrderNum(String orderNumber);

    public ResponseEntity<List<OrderResponseDto>>  getOrderDetailsByUserId(int userId);
}
