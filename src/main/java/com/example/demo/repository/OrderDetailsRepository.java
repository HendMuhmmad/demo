package com.example.demo.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.orm.OrderDetails;

public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Integer> {
    // Custom query methods can be added here if needed
	List<OrderDetails> findByOrderId(int orderId);

}
