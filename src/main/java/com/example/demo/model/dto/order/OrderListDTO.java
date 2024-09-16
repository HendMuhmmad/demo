package com.example.demo.model.dto.order;

import java.util.Date;
import java.util.List;

import com.example.demo.model.dto.orderDetails.OrderDetailsListDTO;
import com.example.demo.model.dto.user.CustomerDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderListDTO {
	String orderNumber;
	double totalPrice;
	Date transaction_date;
	CustomerDTO customer;
	List<OrderDetailsListDTO> items; 
}
