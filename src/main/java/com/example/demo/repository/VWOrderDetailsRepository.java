package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.orm.VW_ORDER_DETAILS;

public interface VWOrderDetailsRepository extends JpaRepository<VW_ORDER_DETAILS, Integer> {
 
}
