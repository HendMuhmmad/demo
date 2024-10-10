package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.orm.ProductTransactionHistory;

public interface ProductTransactionHistoryRepository extends JpaRepository<ProductTransactionHistory, Long>{

}
