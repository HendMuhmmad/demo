package com.example.demo.service.workflow;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.dto.workflow.WFTaskDetailsDto;
import com.example.demo.model.orm.WFProduct;
import com.example.demo.model.orm.workflow.WFInstance;
import com.example.demo.model.orm.workflow.WFTask;
import com.example.demo.repository.workflow.WFTaskRepository;

@Service
@Transactional
public class WFTaskServiceImpl implements WFTaskService {

    @Autowired
    private WFTaskRepository wfTaskRepository;

    @Autowired
    private WFInstanceService wfInstanceService;

    @Autowired
    private WFProductService wfProductService;

    @Override
    public WFTask getWFTaskById(long id) {
	return wfTaskRepository.findById(id)
		.orElseThrow(() -> new EntityNotFoundException("task not found with ID: " + id));
    }

    @Override
    public List<WFTaskDetailsDto<?>> getProductWfTasks(Long userId) {
	List<WFTaskDetailsDto<?>> taskDetailsList = new ArrayList<>();
	List<WFTask> wfTasks = wfTaskRepository.findByAssigneeId(userId);

	for (WFTask wfTask : wfTasks) {
	    WFInstance wfInstance = wfInstanceService.getWFInstanceById(wfTask.getInstanceId());
	    WFProduct wfProduct = wfProductService.findByWfInstanceId(wfInstance.getId());
	    boolean isRunning = wfTask.getActionId() == null;

	    WFTaskDetailsDto<WFProduct> taskDetails = new WFTaskDetailsDto<>(
		    wfInstance.getProcessId(),
		    wfInstance.getId(),
		    wfTask.getAssigneeId(),
		    wfTask.getAssigneeRole(),
		    isRunning,
		    wfProduct);
	    taskDetailsList.add(taskDetails);
	}

	return taskDetailsList;
    }

    @Override
    public WFTaskDetailsDto<?> getTaskById(Long taskId) {

	WFTask wfTask = getWFTaskById(taskId);
	WFInstance wfInstance = wfInstanceService.getWFInstanceById(wfTask.getInstanceId());
	WFProduct wfProduct = wfProductService.findByWfInstanceId(wfInstance.getId());
	boolean isRunning = wfTask.getActionId() == null;

	return new WFTaskDetailsDto<WFProduct>(wfInstance.getProcessId(), wfInstance.getId(), wfTask.getAssigneeId(), wfTask.getAssigneeRole(), isRunning, wfProduct);

    }

    @Override
    public WFTask save(WFTask wfTask) {
	return wfTaskRepository.save(wfTask);
    }

}