package com.example.demo.repository.workflow;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.orm.workflow.WFTask;

public interface WFTaskRepository extends JpaRepository<WFTask, Long> {

	public List<WFTask> findByAssigneeIdAndActionIdIsNull(Long assigneeId);

}