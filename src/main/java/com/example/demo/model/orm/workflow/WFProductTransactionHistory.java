package com.example.demo.model.orm.workflow;


import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ECO_PRODUCT_TRNS_HISTORY")
@Data
public class WFProductTransactionHistory {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "PRODUCT_ID")
    private Long productId;

    @Column(name = "PRODUCT_NAME", nullable = false, length = 100)
    private String productName;

    @Column(name = "PRICE", nullable = false)
    private Double price;

    @Column(name = "COLOR", length = 100)
    private String color;

    @Column(name = "STOCK_QUANTITY")
    private Integer stockQuantity;

    @Column(name = "DESCRIPTION", length = 255)
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATION_DATE")
    private Date creationDate;

    @Column(name = "STATUS", length = 50)
    private String status;
}
