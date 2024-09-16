package com.example.demo.model.dto.orderDetails;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailsListDTO {
	private int id;
	private int quantity;
	private double price;
	private String product_name;
	private String color;
	private String description;
	private int stock_quantity;
	private Date creation_date;
}
