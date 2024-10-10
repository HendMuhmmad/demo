package com.example.demo.repository.workflow.product;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.orm.workflow.product.WFProduct;

public interface WFProductRepository  extends JpaRepository<WFProduct, Long> {

	Optional<WFProduct> findByWfInstanceId(Long instanceId);

	 @Query("select p from WFProduct p, WFInstance i where " +
			    " p.wfInstanceId = i.id " +
			    " and i.status = 1 " +
			    " and (p.productId  = :P_PRODUCT_ID) " +
			    " and (:P_EXCLUDED_INSTANCE_ID = -1L or p.wfInstanceId <> :P_EXCLUDED_INSTANCE_ID) ")
	 List<WFProduct> checkRunningWorkFlowRequests(@Param("P_PRODUCT_ID")Long id, @Param("P_EXCLUDED_INSTANCE_ID")  Long execludedInstanceId);


}
