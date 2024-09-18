package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.orm.Order;


public interface OrderRepository extends JpaRepository<Order, Integer> {

	List<Order> findByUserId(int userId);
	Optional<Order> findByOrderNumber(String orderNumber);

//	List<Order> findByUser_ID(int id);
 }
