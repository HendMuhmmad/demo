package com.example.demo.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.example.demo.exception.BusinessException;
import com.example.demo.model.dto.OrderResponseDto;
import com.example.demo.model.orm.Order;
import com.example.demo.model.orm.OrderDetails;
import com.example.demo.model.orm.Vw_Order_Details;

public interface OrderService {
    Order createOrder(int userId, List<OrderDetails> orderDetails) throws BusinessException;

    public List<Vw_Order_Details> getOrderDetailsByOrderNum(String orderNumber) throws BusinessException;

    public List<Vw_Order_Details> getOrderDetailsByUserId(Integer userId) throws BusinessException;
}
