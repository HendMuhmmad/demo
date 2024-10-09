package com.example.demo.repository.workflow;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.orm.workflow.ProductTransactionHistory;
import com.example.demo.model.orm.workflow.WFProduct;

@Repository
public interface ProductTransactionHistoryRepository extends JpaRepository<ProductTransactionHistory, Long> {
    Optional<WFProduct> findByProductId(Long productId);
}