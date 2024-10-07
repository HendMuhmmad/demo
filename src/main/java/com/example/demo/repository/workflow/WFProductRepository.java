package com.example.demo.repository.workflow;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.model.orm.WFProduct;


@Repository
public interface WFProductRepository extends JpaRepository<WFProduct, Long> {
	
	@Query("SELECT p.productId FROM WFProduct p WHERE p.wfInstanceId = :wfInstanceId")
    Long findProductIdByWfInstanceId(Long wfInstanceId);
}

