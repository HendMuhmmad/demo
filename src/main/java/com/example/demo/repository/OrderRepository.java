package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.orm.Order;


public interface OrderRepository extends JpaRepository<Order, Integer> {

	List<Order> findByUserId(int userId);

//	List<Order> findByUser_ID(int id);
 }
