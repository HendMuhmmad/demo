package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.orm.Vw_Order_Details;

@Repository
public interface VWOrderDetailsRepository extends JpaRepository<Vw_Order_Details, Integer> {

    @Query("SELECT v FROM Vw_Order_Details v WHERE v.orderNumber = :orderNumber")
    List<Vw_Order_Details> findByOrderNumber(@Param("orderNumber") String orderNumber);

}
