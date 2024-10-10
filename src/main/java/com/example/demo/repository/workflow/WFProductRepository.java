package com.example.demo.repository.workflow;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.orm.WFProduct;

@Repository
public interface WFProductRepository extends JpaRepository<WFProduct, Long> {
    WFProduct findByWfInstanceId(Long wfInstanceId);

    @Query("select v from WFProduct v, WFInstance i where " +
	    " v.wfInstanceId = i.id " +
	    " and i.status = 1 " +
	    " and (v.productId in ( :PRODUCT_ID )) ")
    public List<WFProduct> getRunningRequests(@Param("PRODUCT_ID") Long productId);

}
