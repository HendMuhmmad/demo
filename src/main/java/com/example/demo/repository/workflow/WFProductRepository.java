package com.example.demo.repository.workflow;

 

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.orm.WFProduct;
 
@Repository
public interface WFProductRepository extends JpaRepository<WFProduct, Long> {
	  public Optional<WFProduct> findByWfInstanceId(Long instanceId);
   
}