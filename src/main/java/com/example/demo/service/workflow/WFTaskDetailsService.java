package com.example.demo.service.workflow;

import java.util.List;

import com.example.demo.exception.BusinessException;
import com.example.demo.model.orm.workflow.WFTaskDetails;

public interface WFTaskDetailsService {

	WFTaskDetails findByTaskId(Long taskId) throws BusinessException;

	boolean hasOtherRunningTasks(Long productId) throws BusinessException;

	List<WFTaskDetails> findAllActiveTasks(Long taskId);	
}
