package com.example.demo.controller.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.mapper.OrderDetailsMapper;
import com.example.demo.model.dto.OrderResponseDto;
import com.example.demo.model.dto.order.OrderDTO;
import com.example.demo.model.dto.orderDetails.OrderDetailsCreationDTO;
import com.example.demo.service.OrderService;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/getOrderDetailsByOrderNum")
    public ResponseEntity<OrderResponseDto> getOrderDetailsByOrderNum(@RequestParam String orderNumber) {
	return orderService.getOrderDetailsByOrderNum(orderNumber);
    }

    @PostMapping("/createOrder")
    public ResponseEntity<OrderDTO> createOrder(@RequestParam int userId, @RequestBody List<OrderDetailsCreationDTO> orderDetailsDto) {
	OrderDTO createdOrder = orderService.createOrder(userId, OrderDetailsMapper.INSTANCE.mapOrderDetailsCreationDtos(orderDetailsDto));
	return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @GetMapping("/getOrderDetailsByUserId") 
    public ResponseEntity<List<OrderResponseDto>> getOrderDetailsByUserId(@RequestParam int userId) {
	return orderService.getOrderDetailsByUserId(userId);
    }

}
