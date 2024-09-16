package com.example.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.orm.Order_Details;

public interface OrderDetailsRepository extends JpaRepository<Order_Details, Integer> {
    // Custom query methods can be added here if needed
}
