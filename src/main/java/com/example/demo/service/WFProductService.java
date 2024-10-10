package com.example.demo.service;

import java.util.List;

import com.example.demo.enums.workflow.WFAssigneeRoleEnum;
import com.example.demo.enums.workflow.WFProcessesEnum;
import com.example.demo.enums.workflow.WFProductStatusEnum;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.workflow.WFInstance;
import com.example.demo.model.orm.workflow.WFProduct;
import com.example.demo.model.orm.workflow.WFTask;
import com.example.demo.model.orm.workflow.WFTaskDetails;

public interface WFProductService {

	public List<WFTaskDetails> getTasksByAssigneeId(Long assigneeId);
	
	public WFTaskDetails getTaskByTaskId(Long taskId) throws BusinessException;
	
	public void respondToTask(Long taskId, Long assigneeId, String response, String note, String rejectionReason) throws BusinessException;

	WFTask createWFTask(Long instanceId, Long assigneeId, WFAssigneeRoleEnum assigneeRole);

	WFProduct createWFProduct(Product product, Long instanceId, WFProductStatusEnum status);

	List<WFTask> getTasksByInstanceId(Long instanceId);

	public void initWorkflowObjects(Product theProduct, Long loginId, WFProcessesEnum process,
			WFProductStatusEnum status, WFAssigneeRoleEnum superadmin, Long assigneeId);

	WFInstance createWFInstance(WFProductStatusEnum process, Long requesterId);
	
}
