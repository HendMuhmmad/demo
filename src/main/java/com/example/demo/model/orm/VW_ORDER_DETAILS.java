package com.example.demo.model.orm;

 

import java.util.Date;

import javax.persistence.*;

import lombok.Data;

@Entity
@Data
@Table(name = "vw_order_details")
public class VW_ORDER_DETAILS  {

    @Id
    private int id;

    @Column(name = "Order_ID")
    private int orderId;

    @Column(name = "PRODUCT_QUANTITY")
    private int productQuantity;

    @Column(name = "TOTAL_PRICE")
    private double totalPrice;

    @Column(name = "PRODUCT_ID")
    private int productId;

    @Column(name = "USER_ID")
    private int userId;

    @Column(name = "TRANSACTION_DATE")
    @Temporal(TemporalType.DATE)
    private Date transactionDate;

    @Column(name = "PRODUCT_NAME")
    private String productName;

    @Column(name = "PRODUCT_COLOR")
    private String productColor;

    @Column(name = "PRODUCT_DESCRIPTION")
    private String productDescription;

    @Column(name = "CUSTOMER_NAME")
    private String customerName;

    @Column(name = "CUSTOMER_ADDRESS")
    private String customerAddress;

    @Column(name = "CUSTOMER_PHONE")
    private String customerPhone;

    @Column(name = "ORDER_NUMBER")
    private String orderNumber;

 }
