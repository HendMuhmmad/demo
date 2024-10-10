package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.orm.ProductTranHistory;

public interface ProductTranHistoryRepository extends JpaRepository<ProductTranHistory, Long> {

}
