package com.example.demo.service;

import java.util.List;

import com.example.demo.model.dto.order.OrderDTO;
import com.example.demo.model.dto.order.OrderListDTO;
import com.example.demo.model.orm.Order;
import com.example.demo.model.orm.OrderDetails;

public interface OrderService {
	List<OrderListDTO> getAllUserOrders(int id);
    OrderDTO createOrder(int userId,List<OrderDetails> orderDetails);
}
