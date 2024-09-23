package com.example.demo.model.orm;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
<<<<<<< Upstream, based on origin/productTest
=======

import java.util.Date;
>>>>>>> 532a3c4 added product controller Test

@Entity
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "Product_Name",
	    nullable = false)
    private String productName;

    @Column(name = "Price",
	    nullable = false)
    private double price;

    @Column(name = "Color")
    private String color;

    @Column(name = "Stock_Quantity")
    private Integer stockQuantity;

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
