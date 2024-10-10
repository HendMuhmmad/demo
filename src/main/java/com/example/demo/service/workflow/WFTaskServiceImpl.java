package com.example.demo.service.workflow;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.exception.BusinessException;
import com.example.demo.model.orm.workflow.WFTask;
import com.example.demo.repository.workflow.WFTaskRepository;

@Service
@Transactional
public class WFTaskServiceImpl implements WFTaskService {

    @Autowired
    WFTaskRepository wfTaskRepository;
    
	@Override
	public WFTask save(WFTask wfInstance) {
		return wfTaskRepository.save(wfInstance);
	}

	@Override
	public WFTask findById(Long taskId) throws BusinessException{
		return wfTaskRepository.findById(taskId).orElseThrow(() -> new BusinessException("WFTask not found for taskId: " + taskId));
	}

	@Override
	public List<WFTask> findByInstanceIdOrderByIdAsc(Long instanceId) {
		return wfTaskRepository.findByInstanceIdOrderByIdAsc(instanceId);
	}

}
