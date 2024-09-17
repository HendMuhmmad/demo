package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.dto.OrderResponseDto;
import com.example.demo.service.OrderService;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("getOrderDetails/{orderNumber}")
    public ResponseEntity<OrderResponseDto> getOrderDetails(@PathVariable String orderNumber) {
	try {
	    OrderResponseDto orderResponse = orderService.getOrderDetails(orderNumber);
	    return ResponseEntity.ok(orderResponse);
	} catch (RuntimeException e) {
	    return ResponseEntity.notFound().build();
	}
    }
}
