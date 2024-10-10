package com.example.demo.repository.workflow.product;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.orm.workflow.product.WFProduct;

public interface WFProductRepository  extends JpaRepository<WFProduct, Long> {

	Optional<WFProduct> findByWfInstanceId(Long instanceId);

	boolean existsByProductIdAndWfInstanceIdIsNot(Long id, Long wfInstanceId);

}
