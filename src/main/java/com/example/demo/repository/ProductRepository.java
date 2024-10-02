package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.orm.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findTopByOrderByIdDesc(); 
    List<Product> findByWfStatus(Long wfStatus);
}
