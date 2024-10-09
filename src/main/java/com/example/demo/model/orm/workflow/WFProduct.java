package com.example.demo.model.orm.workflow;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.example.demo.model.orm.Product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ECO_WF_PRODUCT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WFProduct{

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


    @Column(name = "PRODUCT_NAME")
    private String productName;

    @Column(name = "PRICE")
    private Double price;

    @Column(name = "COLOR")
    private String color;

    @Column(name = "STOCK_QUANTITY")
    private Integer stockQuantity;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "STATUS")
    private Long status;
    
    public WFProduct( Product product, Long wfInstanceId, Long status) {
        this.productId = product.getId();
        this.wfInstanceId = wfInstanceId;
        this.productName = product.getProductName();
        this.price = product.getPrice();
        this.color = product.getColor();
        this.stockQuantity = product.getStockQuantity();
        this.description = product.getDescription();
        this.status = status;
    }
    
    public WFProduct(Long productId, Long wfInstanceId, String productName, Double price, String color, Integer stockQuantity, String description, Long status) {
        this.productId = productId;
        this.wfInstanceId = wfInstanceId;
        this.productName = productName;
        this.price = price;
        this.color = color;
        this.stockQuantity = stockQuantity;
        this.description = description;
        this.status = status;
    }
}

