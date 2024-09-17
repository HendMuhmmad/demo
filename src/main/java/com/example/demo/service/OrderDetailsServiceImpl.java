package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.orm.OrderDetails;
import com.example.demo.repository.OrderDetailsRepository;

@Service
public class OrderDetailsServiceImpl implements OrderDetailsService {

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Override
    public List<OrderDetails> getAllOrderDetails(int userId, int orderId) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public OrderDetails createOrderDetail(OrderDetails orderDetails) {
	return orderDetailsRepository.save(orderDetails);
    }

}
