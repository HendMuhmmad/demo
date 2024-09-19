package com.example.demo.model.orm;
 
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Order_Details")
public class OrderDetails {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="ORDER_ID")
    private int orderId;

    @Column(name="PRODUCT_ID")
    private int product_id;

    @Column(name = "Quantity", nullable = false)
    private int quantity;
    
 }
