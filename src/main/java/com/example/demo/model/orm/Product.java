package com.example.demo.model.orm;
import javax.persistence.*;

import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "Product_Name", nullable = false)
    private String productName;

    @Column(name = "Price", nullable = false)
    private double price;

    @Column(name = "Color")
    private String color;

    @Column(name = "Stock_Quantity")
    private int stockQuantity;

    @Column(name = "Description")
    private String description;

    @Column(name = "Creation_Date")
    @Temporal(TemporalType.DATE)
    private Date creationDate;
    
    @PrePersist
    protected void onCreate() {
        this.creationDate = new Date(); // Sets the current date
    } 

	

 }

