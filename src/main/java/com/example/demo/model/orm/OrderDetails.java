package com.example.demo.model.orm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "Order_Details")
public class OrderDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ORDER_ID")
    private Integer orderId;

    @Column(name = "PRODUCT_ID")
    private Integer product_id;

    @Column(name = "Quantity",
	    nullable = false)
    private Integer quantity;

}
