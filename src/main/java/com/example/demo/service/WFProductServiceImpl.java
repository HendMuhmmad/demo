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
import com.example.demo.enums.workflow.WFProcessesEnum;
import com.example.demo.enums.workflow.WFProductEnum;
import com.example.demo.enums.workflow.WFStatusEnum;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.dto.TaskRequestDto;
import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.User;
import com.example.demo.model.orm.WFProduct;
import com.example.demo.model.orm.workflow.ProductTrnsHistory;
import com.example.demo.model.orm.workflow.WFInstance;
import com.example.demo.model.orm.workflow.WFTask;
import com.example.demo.model.orm.workflow.WFTaskDetails;
import com.example.demo.repository.ProductRepository;
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
	
	@Autowired
	public ProductRepository productRepository;
	
	@Autowired
	public ProductTrnsHistoryService productTrnsHistoryService;
	
	public void initWFProduct(Product theProduct, long requesterId , long operation) {
				
		Long instanceId = createWFInstance(requesterId,operation);
		
		createWFProduct(theProduct,requesterId,operation,instanceId);

		createWFTask(instanceId);	
	}

	private Long createWFInstance(long requesterId , long operation) {
		WFInstance wfInstance = new WFInstance();
		wfInstance.setProcessId(operation);
		wfInstance.setRequesterId(requesterId);
		wfInstance.setStatus(WFInstanceStatusEnum.RUNNING.getCode());
		wfInstanceRepository.save(wfInstance);
		return wfInstance.getId();
	}

	private void createWFProduct(Product theProduct, long requesterId , long operation,long instanceId) {
		WFProduct wfProduct = new WFProduct();
		wfProduct.setProductId(theProduct.getId());
		wfProduct.setWfInstanceId(instanceId);
		wfProduct.setStatus(operation);
		wfProduct.setColor(theProduct.getColor());
		wfProduct.setPrice(theProduct.getPrice());
		wfProduct.setDescription(theProduct.getDescription());
		wfProduct.setProductName(theProduct.getProductName());
		wfProduct.setStockQuantity(theProduct.getStockQuantity());
		wfProductRepository.save(wfProduct);

	}
	
	private void createWFTask(long instanceId) {
		WFTask wfTask = new WFTask();
		wfTask.setInstanceId(instanceId);
		List<User> users = userService.getUserByRole(RoleEnum.SUPER_ADMIN.getCode());
		wfTask.setAssigneeId(users.get(0).getId());
		wfTask.setAssigneeRole(WFAsigneeRoleEnum.SUPER_ADMIN.getCode());
		wfTaskRepository.save(wfTask);
		
	}
	@Override
	public void respondToRequest(TaskRequestDto taskRequest) {
		WFTask task = validateTaskAndPermissions(taskRequest);
		Optional<WFProduct> TempWFProduct = wfProductRepository.findByWfInstanceId(task.getInstanceId());
		WFProduct wfProduct = null;
		if(TempWFProduct.isPresent()) {
			wfProduct = TempWFProduct.get();
		}
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
			doAction(wfProduct);
		}
		else {
			task.setActionId(WFActionEnum.REJECTED.getCode());
			task.setRefuseReasons(taskRequest.getRefuseReason());
		}
		task.setNotes(taskRequest.getNotes());
		wfTaskRepository.save(task);
	}
	
	private WFTask validateTaskAndPermissions(TaskRequestDto taskRequest) {
	    Optional<WFTask> tempTask = wfTaskRepository.findById(taskRequest.getTaskId());
	    if (!tempTask.isPresent()) {
	        throw new BusinessException("There isn't task with this id");
	    }
	    WFTask task = tempTask.get();
	    if (task.getAssigneeId() != taskRequest.getUserId()) {
	        throw new BusinessException("You don't have the right to respond to this task");
	    }
	    if (task.getActionId() != null) {
	        throw new BusinessException("This task is already done");
	    }
	    return task;
	}

	private void doAction(WFProduct wfProduct) {

		if(WFProcessesEnum.ADD_PRODUCT.getCode() == wfProduct.getStatus()) {
			saveProduct(wfProduct,new Product());
		}
		else if(WFProcessesEnum.DELETE_PRODUCT.getCode() == wfProduct.getStatus()) {
			productRepository.deleteById(wfProduct.getProductId());
		}
		else if(WFProcessesEnum.UPDATE_PRODUCT.getCode() == wfProduct.getStatus()) {
			Optional<Product> productTemp = productRepository.findById(wfProduct.getProductId());
			if(!productTemp.isPresent()) {
				throw new BusinessException("There is no product with this Id");
			}
			Product product = productTemp.get();

			ProductTrnsHistory productHistroy = getHistoryObjectFromProduct(product);
	    	productTrnsHistoryService.createTransaction(productHistroy);
			
	    	saveProduct(wfProduct,product);

		}
	}


	private void saveProduct(WFProduct wfProduct,Product product) {
		product.setColor(wfProduct.getColor());
		product.setDescription(wfProduct.getDescription());
		product.setPrice(wfProduct.getPrice());
		product.setProductName(wfProduct.getProductName());
		product.setStockQuantity(wfProduct.getStockQuantity());

		productRepository.save(product);
	}
	
	private ProductTrnsHistory getHistoryObjectFromProduct(Product theProduct) {
		ProductTrnsHistory productTrnsHistory = new ProductTrnsHistory();
		productTrnsHistory.setColor(theProduct.getColor());
		productTrnsHistory.setDescription(theProduct.getDescription());
		productTrnsHistory.setPrice(theProduct.getPrice());
		productTrnsHistory.setProductId(theProduct.getId());
		productTrnsHistory.setProductName(theProduct.getProductName());
		productTrnsHistory.setStatus(WFProcessesEnum.UPDATE_PRODUCT.getCode());
		productTrnsHistory.setStockQuantity(theProduct.getStockQuantity());
		productTrnsHistory.setCreationDate(new Date());
		return productTrnsHistory;
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
