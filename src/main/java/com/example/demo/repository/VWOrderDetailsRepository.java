package com.example.demo.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.orm.VW_ORDER_DETAILS;

@Repository
public interface VWOrderDetailsRepository extends JpaRepository<VW_ORDER_DETAILS, Integer> {

	@Query("SELECT v FROM VW_ORDER_DETAILS v WHERE v.orderNumber = :orderNumber")
    List<VW_ORDER_DETAILS> findByOrderNumber(@Param("orderNumber") String orderNumber);
    
}
