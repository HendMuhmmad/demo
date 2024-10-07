package com.example.demo.service;

import java.util.List;

import com.example.demo.model.dto.TaskRequestDto;
import com.example.demo.model.orm.workflow.WFTaskDetails;

public interface WFProductService {

	public void initWFProduct(long ProductId, long requesterId , long processId);
	
	public long respondToRequest(TaskRequestDto taskRequest);
	
	public List<WFTaskDetails> getTasksByUserId(long userId);
	
	public WFTaskDetails getTaskByTaskId(long taskId);
	
}
