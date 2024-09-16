package com.example.demo.service;

import java.util.List;

import com.example.demo.model.orm.Order;
import com.example.demo.model.orm.OrderDetails;

public interface OrderDetailsService {

    List<OrderDetails> getAllOrderDetails(int userId, int orderId);
    OrderDetails createOrderDetail(OrderDetails orderDetails);
}
