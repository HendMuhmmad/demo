package com.example.demo.controller.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

	@GetMapping("getOrderDetails/{orderNumber}")
	public ResponseEntity<OrderResponseDto> getOrderDetails(@PathVariable String orderNumber) {
		try {
			OrderResponseDto orderResponse = orderService.getOrderDetails(orderNumber);
			return ResponseEntity.ok(orderResponse);
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	}
   @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestParam int userId, @RequestBody List<OrderDetailsCreationDTO> orderDetailsDto) {
        OrderDTO createdOrder = orderService.createOrder(userId, OrderDetailsMapper.INSTANCE.mapOrderDetailsCreationDtos(orderDetailsDto));
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }
}
