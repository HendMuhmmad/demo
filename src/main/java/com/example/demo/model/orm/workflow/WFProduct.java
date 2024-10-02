package com.example.demo.model.orm.workflow;
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

	public WFProduct(Long productId, Long wfInstanceId) {
		this.productId = productId;
		this.wfInstanceId = wfInstanceId;
	}

}
