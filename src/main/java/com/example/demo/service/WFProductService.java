package com.example.demo.service;

import java.util.List;

import com.example.demo.exception.BusinessException;
import com.example.demo.model.orm.workflow.WFTaskDetails;

public interface WFProductService {

	public List<WFTaskDetails> getTasksByAssigneeId(Long assigneeId);
	
	public void approveTask(Long taskId, Long assigneeId, String note) throws BusinessException;

	public void rejectTask(Long taskId, Long assigneeId, String rejectionReason, String note) throws BusinessException;
	
}
