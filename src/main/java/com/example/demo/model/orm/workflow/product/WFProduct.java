package com.example.demo.model.orm.workflow.product;

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
@Table(name = "ECO_WF_PRODUCT")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WFProduct {

	@Id
	@Column(name = "ID")
	@SequenceGenerator(name = "ECO_WF_PRODUCT_SEQ", sequenceName = "ECO_WF_PRODUCT_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ECO_WF_PRODUCT_SEQ")
	private Long id;

	@Column(name = "PRODUCT_ID")
	private Long productId;

	@Column(name = "WF_INSTANCE_ID")
	private Long wfInstanceId;

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
}
