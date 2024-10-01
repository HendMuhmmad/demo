package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.orm.Order;


public interface OrderRepository extends JpaRepository<Order, Long> {

	List<Order> findByUserId(Long userId);

//	List<Order> findByUser_ID(int id);
 }
