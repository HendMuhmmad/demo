package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.orm.ProductTrnsHistory;
public interface ProductTrnsHistoryRepository extends JpaRepository<ProductTrnsHistory, Long> {

}
 