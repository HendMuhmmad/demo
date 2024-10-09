package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.enums.RoleEnum;
import com.example.demo.enums.workflow.WFActionEnum;
import com.example.demo.enums.workflow.WFInstanceStatusEnum;
import com.example.demo.enums.workflow.WFProductStatusEnum;
import com.example.demo.exception.BusinessException;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.User;
import com.example.demo.model.orm.workflow.ProductTransactionHistory;
import com.example.demo.model.orm.workflow.WFInstance;
import com.example.demo.model.orm.workflow.WFProduct;
import com.example.demo.model.orm.workflow.WFTask;
import com.example.demo.model.orm.workflow.WFTaskDetails;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.workflow.ProductTransactionHistoryRepository;
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
    
    @Autowired
    UserService userService;
    
    @Autowired
    ProductService productService;
    
    @Autowired
    ProductTransactionHistoryRepository productTransactionHistoryRepository;

	@Override
	public List<WFTaskDetails> getTasksByAssigneeId(Long assigneeId) {
		return wfTaskDetailsRepository.findByAssigneeIdAndAction(assigneeId,null);
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
		
		// check using user service TODO
	    if (!userService.getUserById(assigneeId).isPresent()) {
	        throw new BusinessException("User not found for assigneeId: " + assigneeId);
	    }
	    User assignee = userService.getUserById(assigneeId).orElse(null);
	    if (assignee.getRoleId()!=RoleEnum.SUPER_ADMIN.getCode()) throw new BusinessException("User is not a Super Admin");
	    // Verify that the assigneeId matches that of the task
	    if (!task.getAssigneeId().equals(assigneeId)) {
	        throw new BusinessException("Assignee ID does not match the task's assignee ID.");
	    }
	}


	@Override
	public void respondToTask(Long taskId, Long assigneeId, String response, String note, String rejectionReason) throws BusinessException {
	    // Validate input parameters and retrieve necessary objects
	    WFTask task = validateTask(taskId);
	    WFInstance wfInstance = validateInstance(task.getInstanceId());
	    WFProduct wfProduct = validateWFProduct(wfInstance.getId());
	    if (wfProduct.getStatus()!=WFProductStatusEnum.ADDED.getCode())
	    	validateProduct(wfProduct.getProductId());
	    validateAssignee(task, assigneeId);
	    validateResponse(response);
	    // Perform actions for approval or rejection
	    task.setActionId(WFActionEnum.fromString(response)); 
	    task.setNotes(note);  // Set note
	    wfInstance.setStatus(WFInstanceStatusEnum.DONE.getCode());  // Set instance status to APPROVED or REJECTED
	    if (WFActionEnum.fromString(response) == WFActionEnum.REJECTED.getAction())
	    	task.setRefuseReasons(rejectionReason);
	    else if (WFActionEnum.fromString(response) == WFActionEnum.APPROVED.getAction()&&wfProduct.getStatus()==WFProductStatusEnum.ADDED.getCode())
	    	createProduct(wfProduct);
	    else if (WFActionEnum.fromString(response) == WFActionEnum.APPROVED.getAction()&&wfProduct.getStatus()==WFProductStatusEnum.UPDATED.getCode())
	    	updateProduct(wfProduct);
	    else if (WFActionEnum.fromString(response) == WFActionEnum.APPROVED.getAction()&&wfProduct.getStatus()==WFProductStatusEnum.DELETED.getCode())
	    	deleteProduct(wfProduct);
		    
	}

	private void deleteProduct(WFProduct wfProduct) {
		// get initial product
		Long productId =wfProduct.getProductId();
		Product previousProduct = productRepository.findById(productId).orElse(null);
		if (previousProduct == null) throw new BusinessException("Product does not exist");
		
		// map from product to history
		ProductTransactionHistory history = ProductMapper.INSTANCE.maptoHistory(previousProduct);
		
		// status as update
		history.setStatus(WFProductStatusEnum.DELETED.getCode());
		
		// save to history
		productTransactionHistoryRepository.save(history);
		
		// save product
		Product theProduct = ProductMapper.INSTANCE.mapWfProduct(wfProduct);
		productRepository.deleteById(theProduct.getId());
	}

	private void createProduct(WFProduct wfProduct) {
		// save product
		Product theProduct = ProductMapper.INSTANCE.mapWfProduct(wfProduct);
		theProduct.setId(null); // make sure no one tries to overwrite a previous product
		theProduct = productRepository.save(theProduct);
	}
	private void updateProduct(WFProduct wfProduct) throws BusinessException {
		// get initial product
		Long productId =wfProduct.getProductId();
		Product previousProduct = productRepository.findById(productId).orElse(null);
		if (previousProduct == null) throw new BusinessException("Product does not exist");
		// map from product to history
		ProductTransactionHistory history = ProductMapper.INSTANCE.maptoHistory(previousProduct);
		// status as update
		history.setStatus(WFProductStatusEnum.UPDATED.getCode());
		// save to history
		productTransactionHistoryRepository.save(history);
		
		// save product
		Product theProduct = ProductMapper.INSTANCE.mapWfProduct(wfProduct);
		theProduct = productRepository.save(theProduct);
	}
	private void validateResponse(String response) throws BusinessException{
		if (response==null) throw new BusinessException("Select a response");
		if (!(response.equalsIgnoreCase("approve")||!response.equalsIgnoreCase("approved")||!response.equalsIgnoreCase("reject")||!response.equalsIgnoreCase("rejected")))
				throw new BusinessException("Select a valid response");
	}

	@Override
	public WFTaskDetails getTaskByTaskId(Long taskId) throws BusinessException {
		return wfTaskDetailsRepository.findByTaskId(taskId).orElseThrow(() -> new BusinessException("Task Id does not exist"));
		
	}


}
