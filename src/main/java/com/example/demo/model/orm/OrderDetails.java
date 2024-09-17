package com.example.demo.model.orm;
 
import javax.persistence.*;

import lombok.Data;

@Entity
@Data
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
