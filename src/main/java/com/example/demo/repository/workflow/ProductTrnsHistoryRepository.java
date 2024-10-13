package com.example.demo.repository.workflow;
 
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.orm.workflow.ProductTrnsHistory;

public interface ProductTrnsHistoryRepository extends JpaRepository<ProductTrnsHistory, Long> {
 
}