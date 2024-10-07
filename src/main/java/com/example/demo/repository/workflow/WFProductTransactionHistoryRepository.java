package com.example.demo.repository.workflow;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.orm.workflow.WFProductTransactionHistory;



@Repository
public interface WFProductTransactionHistoryRepository extends JpaRepository<WFProductTransactionHistory, Long> {
	
}

