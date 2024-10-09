package com.example.demo.model.orm.workflow;


import javax.persistence.*;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ECO_PRODUCT_TRNS_HISTORY")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductTransactionHistory {

    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "ECO_PRODUCT_TRNS_HISTORY_SEQ", sequenceName = "ECO_PRODUCT_TRNS_HISTORY_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ECO_PRODUCT_TRNS_HISTORY_SEQ")
	
    private Long id;

    @Column(name = "PRODUCT_ID")
    private Long productId;

    @Column(name = "PRODUCT_NAME", nullable = false)
    private String productName;

    @Column(name = "PRICE", nullable = false)
    private Double price;

    @Column(name = "COLOR")
    private String color;

    @Column(name = "STOCK_QUANTITY")
    private Integer stockQuantity;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "CREATION_DATE")
    @Temporal(TemporalType.DATE)
    private Date creationDate;

    @Column(name = "STATUS")
    private Long status;

    public ProductTransactionHistory(Long productId, String productName, Double price, String color, Integer stockQuantity, String description, Date creationDate, Long status) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.color = color;
        this.stockQuantity = stockQuantity;
        this.description = description;
        this.creationDate = creationDate;
        this.status = status;
    }
}