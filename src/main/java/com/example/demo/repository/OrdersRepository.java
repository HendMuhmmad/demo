package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.orm.Order;

public interface OrdersRepository extends JpaRepository<Order, Integer> {
 }
