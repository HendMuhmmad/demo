package com.example.demo.repository.workflow;

 

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.orm.workflow.WFTaskDetails;

 
@Repository
public interface WFTaskDetailsRepository extends JpaRepository<WFTaskDetails, Long> {
	  public List<WFTaskDetails> findByAssigneeId(Long asigneeId);
   
}