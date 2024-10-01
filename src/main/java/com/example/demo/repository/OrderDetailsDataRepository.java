package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.orm.OrderDetailsData;

@Repository
public interface OrderDetailsDataRepository extends JpaRepository<OrderDetailsData, Long> {

    @Query("SELECT v FROM Vw_Order_Details v WHERE v.orderNumber = :orderNumber")
    List<OrderDetailsData> findByOrderNumber(@Param("orderNumber") String orderNumber);

    List<OrderDetailsData> findByUserId(Long userId);

}
