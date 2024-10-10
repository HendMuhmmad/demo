package com.example.demo.service.workflow;

import java.util.List;

import com.example.demo.exception.BusinessException;
import com.example.demo.model.orm.workflow.WFTask;

public interface WFTaskService {

	WFTask save(WFTask wfInstance);

	WFTask findById(Long taskId) throws BusinessException;

	List<WFTask> findByInstanceIdOrderByIdAsc(Long instanceId);	
}
