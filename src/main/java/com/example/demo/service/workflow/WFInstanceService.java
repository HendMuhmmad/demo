package com.example.demo.service.workflow;

import com.example.demo.exception.BusinessException;
import com.example.demo.model.orm.workflow.WFInstance;

public interface WFInstanceService {

	WFInstance save(WFInstance wfInstance);	
	
	WFInstance findById(Long instanceId) throws BusinessException;	
}
