package com.example.demo.model.orm;

import javax.persistence.*;

import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "ordesr")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "User_ID", nullable = false)
    private User user;

    @Column(name = "Transaction_Date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date transactionDate;

    @Column(name = "Order_Number", nullable = false)
    private String orderNumber;

 }
