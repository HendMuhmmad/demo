package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.orm.Vw_Order_Details;

public interface VWOrderDetailsRepository extends JpaRepository<Vw_Order_Details, Integer> {
 
}
