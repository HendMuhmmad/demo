package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.enums.RoleEnum;
import com.example.demo.enums.workflow.WFActionEnum;
import com.example.demo.enums.workflow.WFInstanceStatusEnum;
import com.example.demo.enums.workflow.WFStatusEnum;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.User;
import com.example.demo.model.orm.workflow.WFInstance;
import com.example.demo.model.orm.workflow.WFProduct;
import com.example.demo.model.orm.workflow.WFTask;
import com.example.demo.model.orm.workflow.WFTaskDetails;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.workflow.WFInstanceRepository;
import com.example.demo.repository.workflow.WFProductRepository;
import com.example.demo.repository.workflow.WFTaskDetailsRepository;
import com.example.demo.repository.workflow.WFTaskRepository;
@Service
@Transactional
public class WFProductServiceImpl implements WFProductService{
	
	@Autowired
	WFTaskDetailsRepository wfTaskDetailsRepository;
    
    @Autowired
    public WFProductRepository wfProductRepository;
    
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    WFInstanceRepository wfInstanceRepository;
    
    @Autowired 
    WFTaskRepository wfTaskRepository;
    
    @Autowired
    UserRepository userRepository;

	@Override
	public List<WFTaskDetails> getTasksByAssigneeId(Long assigneeId) {
		return wfTaskDetailsRepository.findByAssigneeIdAndWfStatus(assigneeId,WFStatusEnum.UNDERAPPROVAL.getCode());
	}

	@Override
	public void approveTask(Long taskId, Long assigneeId, String note) throws BusinessException {
	    WFTask task = validateTask(taskId);
	    WFInstance wfInstance = validateInstance(task.getInstanceId());
	    WFProduct wfProduct = validateWFProduct(wfInstance.getId());
	    Product product = validateProduct(wfProduct.getProductId());
	    validateAssignee(task, assigneeId);

	    // Perform actions
	    task.setActionId(WFActionEnum.APPROVED.getAction());
	    task.setNotes(note);
	    wfInstance.setStatus(WFInstanceStatusEnum.DONE.getCode());
	    product.setWfStatus(WFStatusEnum.APPROVED.getCode());
	}

	private WFTask validateTask(Long taskId) throws BusinessException {
		if (taskId==null) throw new BusinessException("No TaskId");
		WFTask wfTask = wfTaskRepository.findById(taskId)
		        .orElseThrow(() -> new BusinessException("Task not found for taskId: " + taskId));
	    if (wfTask.getActionId()==WFActionEnum.REJECTED.getAction()||wfTask.getActionId()==WFActionEnum.APPROVED.getAction()) {
	    	throw new BusinessException("This task was already evaluated");
	    }
		return wfTask;
	}

	private WFInstance validateInstance(Long instanceId) throws BusinessException {
	    return wfInstanceRepository.findById(instanceId)
	        .orElseThrow(() -> new BusinessException("WFInstance not found for instanceId: " + instanceId));
	}

	private WFProduct validateWFProduct(Long instanceId) throws BusinessException {
	    return wfProductRepository.findByWfInstanceId(instanceId)
	        .orElseThrow(() -> new BusinessException("WFProduct not found for WFInstanceId: " + instanceId));
	}

	private Product validateProduct(Long productId) throws BusinessException {
	    return productRepository.findById(productId)
	        .orElseThrow(() -> new BusinessException("Product not found for productId: " + productId));
	}

	private void validateAssignee(WFTask task, Long assigneeId) throws BusinessException {
		if (assigneeId==null) throw new BusinessException("No loginId.");
		// Verify that the user exists
		
	    if (!userRepository.existsById(assigneeId)) {
	        throw new BusinessException("User not found for assigneeId: " + assigneeId);
	    }
	    User assignee = userRepository.findById(assigneeId).orElse(null);
	    if (assignee.getRoleId()!=RoleEnum.SUPER_ADMIN.getCode()) throw new BusinessException("User is not a Super Admin");
	    // Verify that the assigneeId matches that of the task
	    if (!task.getAssigneeId().equals(assigneeId)) {
	        throw new BusinessException("Assignee ID does not match the task's assignee ID.");
	    }
	}


	@Override
	public void rejectTask(Long taskId, Long assigneeId, String rejectionReason, String note) throws BusinessException {
	    // Validate input parameters and retrieve necessary objects
	    WFTask task = validateTask(taskId);
	    WFInstance wfInstance = validateInstance(task.getInstanceId());
	    WFProduct wfProduct = validateWFProduct(wfInstance.getId());
	    Product product = validateProduct(wfProduct.getProductId());
	    validateAssignee(task, assigneeId);

	    // Perform actions for rejection
	    task.setActionId(WFActionEnum.REJECTED.getAction());  // Assuming there is a REJECTED action
	    task.setNotes(note);  // Set the notes for the rejection
	    task.setRefuseReasons(rejectionReason);
	    wfInstance.setStatus(WFInstanceStatusEnum.DONE.getCode());  // Set instance status to REJECTED
	    product.setWfStatus(WFStatusEnum.REJECTED.getCode());  // Set product status to REJECTED
	}


}
