package com.example.demo.repository.workflow;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.orm.workflow.WFTaskDetails;

public interface WFTaskDetailsRepository extends JpaRepository<WFTaskDetails, Long> {
     public List<WFTaskDetails> findByAssigneeId(long userId);
}
