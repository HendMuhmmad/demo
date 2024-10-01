package com.example.demo.service;

import java.util.List;

import com.example.demo.exception.BusinessException;
import com.example.demo.model.orm.Order;
import com.example.demo.model.orm.OrderDetails;
import com.example.demo.model.orm.OrderDetailsData;

public interface OrderService {
    Order createOrder(Long userId, List<OrderDetails> orderDetails) throws BusinessException;

    public List<OrderDetailsData> getOrderDetailsByOrderNum(String orderNumber) throws BusinessException;

    public List<OrderDetailsData> getOrderDetailsByUserId(Long userId) throws BusinessException;
}
