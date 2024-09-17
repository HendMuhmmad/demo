package com.example.demo.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.orm.Order_Details;
import com.example.demo.model.orm.VW_ORDER_DETAILS;

public interface OrderDetailsRepository extends JpaRepository<Order_Details, Integer> {

//    List<VW_ORDER_DETAILS> findByOrderNumber(String orderNumber);

}
