package com.example.demo.service.workflow;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.enums.workflow.WFInstanceStatusEnum;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.orm.workflow.WFTaskDetails;
import com.example.demo.repository.workflow.WFTaskDetailsRepository;
@Service
@Transactional
public class WFTaskDetailsServiceImpl implements WFTaskDetailsService{
	
	@Autowired
	WFTaskDetailsRepository wfTaskDetailsRepository;

	@Override
	public WFTaskDetails findByTaskId(Long taskId) throws BusinessException {
		return wfTaskDetailsRepository.findByTaskId(taskId).orElseThrow(() -> new BusinessException("Task Id does not exist"));
	}
	@Override
	public List<WFTaskDetails> findAllActiveTasks(Long taskId) {
		return wfTaskDetailsRepository.findByAssigneeIdAndAction(taskId,null);
	}
	@Override
	public boolean hasOtherRunningTasks(Long productId) {
		return wfTaskDetailsRepository.countByProductIdAndInstanceStatus(productId, WFInstanceStatusEnum.RUNNING.getCode())>0;
	}

	
  

}
