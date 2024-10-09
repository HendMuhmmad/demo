package com.example.demo.repository.workflow;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.orm.workflow.WFTaskDetails;

public interface WFTaskDetailsRepository extends JpaRepository<WFTaskDetails, Long> {
    Optional<WFTaskDetails> findByTaskId(Long taskId);
    List<WFTaskDetails> findByAssigneeIdAndAction(Long assigneeId, String Action);

}