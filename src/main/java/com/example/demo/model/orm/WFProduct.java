package com.example.demo.model.orm;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "ECO_WF_PRODUCT")
@Data
public class WFProduct {

    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "ECO_WF_PRODUCT_SEQ",
        sequenceName = "ECO_WF_PRODUCT_SEQ",
        allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
        generator = "ECO_WF_PRODUCT_SEQ")
    private Long id;

    @Column(name = "PRODUCT_ID")
    private Long productId;

    @Column(name = "WF_INSTANCE_ID")
    private Long wfInstanceId;

    @Column(name = "PRODUCT_NAME", length = 255)
    private String productName;

    @Column(name = "PRICE", precision = 10, scale = 2)
    private Double price;

    @Column(name = "COLOR", length = 50)
    private String color;

    @Column(name = "STOCK_QUANTITY")
    private Integer stockQuantity;

    @Column(name = "DESCRIPTION", length = 255)
    private String description;

    @Column(name = "STATUS")
    private Integer status;
}
