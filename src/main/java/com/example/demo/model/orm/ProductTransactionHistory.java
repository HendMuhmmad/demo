package com.example.demo.model.orm;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "ECO_PRODUCT_TRNS_HISTORY")
public class ProductTransactionHistory {
	@Id
	@Column(name = "ID")
	@SequenceGenerator(name = "ECO_PRODUCT_TRNS_HISTORY_SEQ", sequenceName = "ECO_PRODUCT_TRNS_HISTORY_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ECO_PRODUCT_TRNS_HISTORY_SEQ")
	private Long id;

	@Column(name = "PRODUCT_ID")
	private Long productId;

	@Column(name = "Product_Name", nullable = false)
	private String productName;

	@Column(name = "Price", nullable = false)
	private Double price;

	@Column(name = "Color")
	private String color;

	@Column(name = "Stock_Quantity")
	private Integer stockQuantity;

	@Column(name = "Description")
	private String description;

	@Column(name = "STATUS")
	private Integer status;

	@Column(name = "Creation_Date")
	@Temporal(TemporalType.DATE)
	private Date creationDate;

}
