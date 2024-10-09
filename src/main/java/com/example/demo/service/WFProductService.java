package com.example.demo.service;

import java.util.List;

import com.example.demo.exception.BusinessException;
import com.example.demo.model.orm.workflow.WFTaskDetails;

public interface WFProductService {

	public List<WFTaskDetails> getTasksByAssigneeId(Long assigneeId);
	
	public WFTaskDetails getTaskByTaskId(Long taskId) throws BusinessException;
	
	public void respondToTask(Long taskId, Long assigneeId, String response, String note, String rejectionReason) throws BusinessException;
	
}
