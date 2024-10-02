package com.example.demo.model.orm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ECO_ORDER_DETAILS")
public class OrderDetails {
    @Id
    @SequenceGenerator(name = "ECO_ORDER_DETAILS_SEQ",
	    sequenceName = "ECO_ORDER_DETAILS_SEQ",
	    allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
	    generator = "ECO_ORDER_DETAILS_SEQ")
    private Long id;

    @Column(name = "ORDER_ID")
    private Long orderId;

    @Column(name = "PRODUCT_ID")
    private Long product_id;

    @Column(name = "Quantity",
	    nullable = false)
    private Integer quantity;

}
