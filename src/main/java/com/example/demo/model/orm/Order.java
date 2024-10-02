package com.example.demo.model.orm;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "eco_orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="User_ID")
    private Long userId;

    @Column(name = "Transaction_Date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date transactionDate;

    @Column(name = "Order_Number", nullable = false)
    private String orderNumber;
    
    @Transient
    private double totalPrice;

	public Order(Long userId, Date transactionDate, String orderNumber) {
		this.userId = userId;
		this.transactionDate = transactionDate;
		this.orderNumber = orderNumber;
	}

	public Order(Long id, Long userId, Date transactionDate) {
		this.id = id;
		this.userId = userId;
		this.transactionDate = transactionDate;
	}
    
	
    
 }
