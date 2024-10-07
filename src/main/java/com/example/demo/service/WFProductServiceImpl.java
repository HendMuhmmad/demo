package com.example.demo.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.enums.RoleEnum;
import com.example.demo.enums.workflow.WFActionEnum;
import com.example.demo.enums.workflow.WFAsigneeRoleEnum;
import com.example.demo.enums.workflow.WFInstanceStatusEnum;
import com.example.demo.enums.workflow.WFProductEnum;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.dto.TaskRequestDto;
import com.example.demo.model.orm.User;
import com.example.demo.model.orm.WFProduct;
import com.example.demo.model.orm.workflow.WFInstance;
import com.example.demo.model.orm.workflow.WFTask;
import com.example.demo.model.orm.workflow.WFTaskDetails;
import com.example.demo.repository.workflow.WFInstanceRepository;
import com.example.demo.repository.workflow.WFProductRepository;
import com.example.demo.repository.workflow.WFTaskDetailsRepository;
import com.example.demo.repository.workflow.WFTaskRepository;


@Service
public class WFProductServiceImpl implements WFProductService {

	@Autowired
	private WFInstanceRepository wfInstanceRepository;

	@Autowired
	private WFProductRepository wfProductRepository;
	
	@Autowired
	private WFTaskRepository wfTaskRepository;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private WFTaskDetailsRepository wfTaskDetailsRepository;
	
	public void initWFProduct(long ProductId, long requesterId, long processId) {
				
		WFInstance wfInstance = new WFInstance();
		wfInstance.setProcessId(processId);
		wfInstance.setRequesterId(requesterId);
		wfInstance.setStatus(WFInstanceStatusEnum.RUNNING.getCode());
		wfInstanceRepository.save(wfInstance);
		
		WFProduct wfProduct = new WFProduct();
		wfProduct.setProductId(ProductId);
		wfProduct.setWfInstanceId(wfInstance.getId());
		wfProduct.setStatus(WFProductEnum.ADDED.getCode());
		wfProductRepository.save(wfProduct);
		
		WFTask wfTask = new WFTask();
		wfTask.setActionId(WFActionEnum.APPROVED.getCode());
		wfTask.setInstanceId(wfInstance.getId());
		List<User> users = userService.getUserByRole(RoleEnum.SUPER_ADMIN.getCode());
		wfTask.setAssigneeId(users.get(0).getId());
		wfTask.setAssigneeRole(WFAsigneeRoleEnum.SUPER_ADMIN.getCode());
		wfTaskRepository.save(wfTask);
		
	}



	@Override
	public long respondToRequest(TaskRequestDto taskRequest) {
		Optional<WFTask> tempTask = wfTaskRepository.findById(taskRequest.getTaskId());
		if(!tempTask.isPresent()) {
			throw new BusinessException("There isnt task with this id");
		}
		WFTask task = tempTask.get();
		if(task.getAssigneeId() != taskRequest.getUserId()) {
			throw new BusinessException("You dont have the right to respond to this task");
		}
		
		long productId = wfProductRepository.findProductIdByWfInstanceId(task.getInstanceId());
		
		// Update the instance status
		Optional<WFInstance> tempInstance = wfInstanceRepository.findById(task.getInstanceId());
		if(!tempInstance.isPresent()) {
			throw new BusinessException("There is an error in the instance");
		}
		WFInstance wfInstance = tempInstance.get();
		wfInstance.setStatus(WFInstanceStatusEnum.DONE.getCode());
		wfInstanceRepository.save(wfInstance);
		
		
		//update the task status
		task.setActionDate(new Date());
		if(taskRequest.getIsApproved()) {
			task.setActionId(WFActionEnum.APPROVED.getCode());
		}
		else {
			task.setActionId(WFActionEnum.REJECTED.getCode());
			task.setRefuseReasons(taskRequest.getRefuseReason());
		}
		task.setNotes(taskRequest.getNotes());
		return productId;
	}



	@Override
	public List<WFTaskDetails> getTasksByUserId(long userId) {
	    List<WFTaskDetails> tasks = wfTaskDetailsRepository.findByAssigneeId(userId);
	    return tasks;
	}
	
	@Override
	public WFTaskDetails getTaskByTaskId(long taskId) {
	    return wfTaskDetailsRepository.findById(taskId)
	        .orElseThrow(() -> new BusinessException("Task with ID " + taskId + " not found"));
	}



}
