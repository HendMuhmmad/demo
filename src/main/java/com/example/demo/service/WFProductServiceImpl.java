package com.example.demo.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.enums.RoleEnum;
import com.example.demo.enums.workflow.WFActionEnum;
import com.example.demo.enums.workflow.WFAssigneeRoleEnum;
import com.example.demo.enums.workflow.WFInstanceStatusEnum;
import com.example.demo.enums.workflow.WFProcessesEnum;
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
    WFProductRepository wfProductRepository;
     
    @Autowired
    WFInstanceRepository wfInstanceRepository;
    
    @Autowired 
    WFTaskRepository wfTaskRepository;
    

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


	private void validateAssignee(WFTask task, Long loginId) throws BusinessException {
		if (loginId==null) throw new BusinessException("No loginId.");
		// Verify that the user exists
		User user = userService.getUserById(loginId).orElse(null);
	    if (user == null) {
	        throw new BusinessException("User not found for loginId: " + loginId);
	    }
	    if (user.getRoleId()!=RoleEnum.SUPER_ADMIN.getCode()) throw new BusinessException("User is not a Super Admin");
	    // Verify that the assigneeId matches that of the task
	    if (!task.getAssigneeId().equals(loginId)) {
	        throw new BusinessException("Login ID does not match the task's assignee ID.");
	    }
	}


	@Override
	public void respondToTask(Long taskId, Long assigneeId, String response, String note, String rejectionReason) throws BusinessException {
	    // Validate input parameters and retrieve necessary objects
	    WFTask task = validateTask(taskId);
	    WFInstance wfInstance = validateInstance(task.getInstanceId());
	    WFProduct wfProduct = validateWFProduct(wfInstance.getId());
	    validateAssignee(task, assigneeId);
	    validateResponse(response);
	    // Perform actions for approval or rejection
	    task.setActionId(WFActionEnum.fromString(response)); 
	    task.setNotes(note);  // Set note
	    wfInstance.setStatus(WFInstanceStatusEnum.DONE.getCode());  // Set instance status to APPROVED or REJECTED
	    if (WFActionEnum.fromString(response) == WFActionEnum.REJECTED.getAction())
	    	task.setRefuseReasons(rejectionReason);
	    else if (WFActionEnum.APPROVED.getAction().equals(task.getActionId())) {
	        handleApproval(wfProduct, assigneeId);
	    }
	}

	private void handleApproval(WFProduct wfProduct, Long loginId) {
		 switch (WFProductStatusEnum.fromCode(wfProduct.getStatus())) {
	        case ADDED:
	            createProduct(wfProduct,loginId);
	            break;
	        case UPDATED:
	            updateProduct(wfProduct,loginId);
	            break;
	        case DELETED:
	            deleteProduct(wfProduct,loginId);
	            break;
	        default:
	            throw new BusinessException("Invalid product status for approval");
	    }
		
	}

	private void deleteProduct(WFProduct wfProduct, Long loginId) {
		Product previousProduct = validateProduct(wfProduct);
		WFProductStatusEnum wfProductStatusEnum = WFProductStatusEnum.DELETED;
		addToHistory(previousProduct, wfProductStatusEnum);
		// delete product
		productService.deleteProduct(previousProduct.getId(), loginId);
	}

	private Product validateProduct(WFProduct wfProduct) {
		// get initial product
		Long productId =wfProduct.getProductId();
		Product previousProduct = productService.findById(productId);
		if (previousProduct == null) throw new BusinessException("Product does not exist");
		return previousProduct;
	}

	private void createProduct(WFProduct wfProduct, Long loginId) {
		// save product
		Product theProduct = ProductMapper.INSTANCE.mapWfProduct(wfProduct);
		theProduct.setId(null); // make sure no one tries to overwrite a previous product
		productService.save(theProduct, loginId);
	}
	private void updateProduct(WFProduct wfProduct, Long loginId) throws BusinessException {
		Product previousProduct = validateProduct(wfProduct);
		// map from product to history
		WFProductStatusEnum wfProductStatusEnum = WFProductStatusEnum.UPDATED;
		addToHistory(previousProduct, wfProductStatusEnum);
		// save product
		Product theProduct = ProductMapper.INSTANCE.mapWfProduct(wfProduct);
		productService.save(theProduct, loginId);
	}

	private void addToHistory(Product previousProduct, WFProductStatusEnum wfProductStatusEnum) {
		// map from product to history
		ProductTransactionHistory history = ProductMapper.INSTANCE.maptoHistory(previousProduct);
		// update status
		history.setStatus(wfProductStatusEnum.getCode());
		// save to history
		productTransactionHistoryRepository.save(history);
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
	@Override
	public void initWorkflowObjects(Product product, Long loginId, WFProcessesEnum process, 
			WFProductStatusEnum wfProductStatus,  WFAssigneeRoleEnum assigneeRole, 
			Long assigneeId) {
		// create process instance
		WFInstance wfInstance = createWFInstance(wfProductStatus, loginId);
		// create process instance task
		createWFTask(wfInstance.getId(), assigneeId, assigneeRole);
		// create wfProduct
		createWFProduct(product,wfInstance.getId(),wfProductStatus);			
	}

	@Override
    public WFInstance createWFInstance(WFProductStatusEnum process, Long requesterId) {
        WFInstance wfInstance = new WFInstance();
        wfInstance.setProcessId(process.getCode());
        wfInstance.setRequesterId(requesterId);
        wfInstance.setRequestDate(new Date()); 
        wfInstance.setStatus(WFInstanceStatusEnum.RUNNING.getCode());

        return wfInstanceRepository.save(wfInstance); // Save the instance
    }
    @Override
    public WFTask createWFTask(Long instanceId, Long assigneeId, WFAssigneeRoleEnum assigneeRole) {
        WFTask wfTask = new WFTask(instanceId, assigneeId, assigneeRole.getRole(), new Date());
        return wfTaskRepository.save(wfTask); // Save the task
    }
    @Override
    public WFProduct createWFProduct(Product product,Long instanceId, WFProductStatusEnum status) {
		WFProduct wfProduct = new WFProduct(product,instanceId,status.getCode());
        return wfProductRepository.save(wfProduct); // Save the task
    }
    @Override
    public List<WFTask> getTasksByInstanceId(Long instanceId) {
        return wfTaskRepository.findByInstanceIdOrderByIdAsc(instanceId); // Fetch tasks by instance ID
    }

	@Override
	public boolean hasOtherRunningTasks(Long productId) {
		return wfTaskDetailsRepository.countByProductIdAndInstanceStatus(productId, WFInstanceStatusEnum.RUNNING.getCode())>0;
	}
    


}
