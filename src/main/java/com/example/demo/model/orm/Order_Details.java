package com.example.demo.model.orm;
 
import javax.persistence.*;

import lombok.Data;

@Entity
@Data
@Table(name = "Order_Details")
public class Order_Details {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "Order_ID", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "Product_ID", nullable = false)
    private Product product;

    @Column(name = "Quantity", nullable = false)
    private int quantity;

 }
