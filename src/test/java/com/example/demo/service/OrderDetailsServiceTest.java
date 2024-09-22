package com.example.demo.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
        orderDetails.setOrderId(1);
        orderDetails.setProduct_id(101);
        orderDetails.setQuantity(5);
        Mockito.when(orderDetailsRepository.save(any(OrderDetails.class))).thenReturn(orderDetails);
        OrderDetails createdOrderDetails = orderDetailsService.createOrderDetail(orderDetails);
        assertNotNull(createdOrderDetails);
        assertEquals(1, createdOrderDetails.getOrderId());
        assertEquals(101, createdOrderDetails.getProduct_id());
        assertEquals(5, createdOrderDetails.getQuantity());

		
	}

}
