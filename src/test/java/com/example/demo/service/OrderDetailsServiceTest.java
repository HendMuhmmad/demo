package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.example.demo.model.orm.OrderDetails;
import com.example.demo.repository.OrderDetailsRepository;

@SpringBootTest
public class OrderDetailsServiceTest {

    @MockBean
    OrderDetailsRepository orderDetailsRepository;

    @Autowired
    OrderDetailsService orderDetailsService;

    @Test
    void createOrderDetails() {
	OrderDetails orderDetails = new OrderDetails();
	orderDetails.setOrderId(1L);
	orderDetails.setProduct_id(101L);
	orderDetails.setQuantity(5);
	Mockito.when(orderDetailsRepository.save(any(OrderDetails.class))).thenReturn(orderDetails);
	OrderDetails createdOrderDetails = orderDetailsService.createOrderDetail(orderDetails);
	assertNotNull(createdOrderDetails);
	assertEquals(1L, (long) createdOrderDetails.getOrderId());
	assertEquals(101L, (long) createdOrderDetails.getProduct_id());
	assertEquals(5L, (long) createdOrderDetails.getQuantity());

    }

}
