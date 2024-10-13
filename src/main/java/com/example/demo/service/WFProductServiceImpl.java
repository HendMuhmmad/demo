package com.example.demo.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.enums.RoleEnum;
import com.example.demo.enums.WFActionEnum;
import com.example.demo.enums.WFAsigneeRoleEnum;
import com.example.demo.enums.WFProductActionStatusEnum;
import com.example.demo.enums.workflow.WFInstanceStatusEnum;
import com.example.demo.enums.workflow.WFProcessesEnum;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.ProductTrnsHistory;
import com.example.demo.model.orm.ProductTrnsHistory;
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
  private WFInstanceRepository wFInstanceRepository;
  @Autowired
  private WFProductRepository wFProductRepository;
  @Autowired
  private UserService userService;
  @Autowired
  private ProductService productService;
  @Autowired
  private WFTaskRepository wFTaskRepository;
  @Autowired
  private WFTaskDetailsRepository wFTaskDetailsRepository;
  
  @Autowired 
  private ProductTrnsHistoryService productTrnsHistoryService;
  public Long initWFProduct(long PROCESS_ID, long requesterId, WFProduct wfProduct, WFProductActionStatusEnum actionStatus) {
	Long roleId = userService.getUserById(requesterId).get().getRoleId();
	if (roleId != RoleEnum.SUPER_ADMIN.getCode()) {
		 throw new BusinessException("You are not authorized to do this action...");
	}
    //Create WF Instance
	 
    WFInstance wfInstace = new WFInstance();
    wfInstace.setProcessId(PROCESS_ID);
    wfInstace.setRequesterId(requesterId);
    wfInstace.setStatus(WFInstanceStatusEnum.RUNNING.getCode());
    wFInstanceRepository.save(wfInstace);

    //Create WF Product

    wfProduct.setWfInstanceId(wfInstace.getId());
    wFProductRepository.save(wfProduct);
    //		

    //Create WF Task

    WFTask wfTask = new WFTask();
    wfTask.setInstanceId(wfInstace.getId());
    wfTask.setAssigneeRole(WFAsigneeRoleEnum.SUPER_ADMIN.getCode());
    //i think this should be another service called TaskAssigningPolicy
    //make sure no service can talk to another service repoistory
    List < User > users = userService.getUsesrByRole(RoleEnum.SUPER_ADMIN.getCode());
    if (users.isEmpty()) {
      throw new BusinessException("No users found with the role of SUPER_ADMIN.");
    }
    wfTask.setAssigneeId(users.get(0).getId());
    //Ends
    wfTask.setActionId(null);
    wFTaskRepository.save(wfTask);
    return wfTask.getId();
  }
  @Override
  public Long addWFProduct(long requesterId, Product product) {
    WFProductActionStatusEnum actionStatus = WFProductActionStatusEnum.ADD;
    long PROCESS_ID = WFProcessesEnum.ADD_PRODUCT.getCode();
    WFProduct wfProduct = WFProduct.builder()
      .productName(product.getProductName())
      .price(product.getPrice())
      .color(product.getColor())
      .stockQuantity(product.getStockQuantity())
      .description(product.getDescription())
      .status(actionStatus.getCode())
      .build();

    return initWFProduct(PROCESS_ID, requesterId, wfProduct, actionStatus);

  }
  
  
  @Override
  public Long updateWFProduct(long requesterId, long oldProductId, Product product) {
    WFProductActionStatusEnum actionStatus = WFProductActionStatusEnum.UPDATE;
    long PROCESS_ID = WFProcessesEnum.UPDATE_PRODUCT.getCode();
    WFProduct wfProduct = WFProduct.builder()
      .productId(oldProductId)
      .productName(product.getProductName())
      .price(product.getPrice())
      .color(product.getColor())
      .stockQuantity(product.getStockQuantity())
      .description(product.getDescription())
      .status(actionStatus.getCode())
      .build();

    return initWFProduct(PROCESS_ID, requesterId, wfProduct, actionStatus);

  }
  @Override
  public Long deleteWFProduct(long requesterId, long oldProductId) {
    WFProductActionStatusEnum actionStatus = WFProductActionStatusEnum.DELETE;
    long PROCESS_ID = WFProcessesEnum.DELETE_PRODUCT.getCode();
    WFProduct wfProduct = WFProduct.builder()
      .productId(oldProductId)
      .status(actionStatus.getCode())
      .build();

    return initWFProduct(PROCESS_ID, requesterId, wfProduct, actionStatus);

  }

  @Transactional
  public void changeRequestStatus(long taskId, long userId, boolean isApproved) {
    //		User user = userService.getUserById(asigneeId);
    Optional < WFTask > wfTask = wFTaskRepository.findById(taskId);
    if (!wfTask.isPresent()) throw new BusinessException("Please Enter Correct Task Id");
    WFTask currentTask = wfTask.get();
    if (currentTask.getActionId() != null) throw new BusinessException("This task already done..");
    if (currentTask.getAssigneeId() != userId) throw new BusinessException("You are not authorized to perform this action...");

    long instanceId = currentTask.getInstanceId();

    Optional < WFInstance > wfInstance = wFInstanceRepository.findById(instanceId);
    if (!wfInstance.isPresent()) throw new BusinessException("Something went wrong ...");
    WFInstance currentWfInstance = wfInstance.get();
    currentWfInstance.setStatus(WFInstanceStatusEnum.DONE.getCode());
    wFInstanceRepository.save(currentWfInstance);

    if (isApproved) {
      currentTask.setActionId(WFActionEnum.APPROVED.getCode());
    } else {
      currentTask.setActionId(WFActionEnum.REJECTED.getCode());
    }

    currentTask.setActionDate(new Date());

    wFTaskRepository.save(currentTask);

    WFProduct currentWFProduct = this.getWfProductByTaskId(taskId);
    if (isApproved) {
    handleProductChangeRequest(currentWFProduct, userId);
    }
  }

  void handleProductChangeRequest(WFProduct currentWFProduct, long userId) {
	 
		Long id = currentWFProduct.getProductId();
        if (id != null) {
        	Product oldProduct = productService.findbyId(id);
        	ProductTrnsHistory productTrnsHistory = ProductTrnsHistory.builder()
        			.productId(id)
        	        .productName(oldProduct.getProductName())
        	        .price(oldProduct.getPrice())
        	        .color(oldProduct.getColor())
        	        .stockQuantity(oldProduct.getStockQuantity())
        	        .description(oldProduct.getDescription())
        	        .build();
   
        		productTrnsHistoryService.createTransaction(productTrnsHistory);
        }  
	  
	  
    if (currentWFProduct.getStatus() == WFProductActionStatusEnum.ADD.getCode() || currentWFProduct.getStatus() == WFProductActionStatusEnum.UPDATE.getCode()) {
    	Product product = Product.builder()
        .productName(currentWFProduct.getProductName())
        .price(currentWFProduct.getPrice())
        .color(currentWFProduct.getColor())
        .stockQuantity(currentWFProduct.getStockQuantity())
        .description(currentWFProduct.getDescription())
        .build();
    	if (currentWFProduct.getStatus() == WFProductActionStatusEnum.UPDATE.getCode()) {
    		product.setId(id);
    	}
      productService.save(product, userId);
    } else if (currentWFProduct.getStatus() == WFProductActionStatusEnum.DELETE.getCode()) {
    	if (id == null)   throw new BusinessException("There is no product with this id...");
    	productService.deleteProduct(id, userId);
    }  else {
      throw new BusinessException("Error Handling Request...");
    }
  }

  @Override
  public long getProductIdByTaskId(long taskId) {
    Optional < WFTask > wfTask = wFTaskRepository.findById(taskId);
    if (!wfTask.isPresent()) throw new BusinessException("Please Enter Correct Task Id");
    WFTask currentTask = wfTask.get();

    long instanceId = currentTask.getInstanceId();

    Optional < WFProduct > wfProduct = wFProductRepository.findByWfInstanceId(instanceId);
    if (!wfProduct.isPresent()) throw new BusinessException("Something went wrong ...");
    WFProduct currentWFProduct = wfProduct.get();
    
    return currentWFProduct.getProductId();

  }

  public WFProduct getWfProductByTaskId(long taskId) {
    Optional < WFTask > wfTask = wFTaskRepository.findById(taskId);
    if (!wfTask.isPresent()) throw new BusinessException("Please Enter Correct Task Id");
    WFTask currentTask = wfTask.get();

    long instanceId = currentTask.getInstanceId();

    Optional < WFProduct > wfProduct = wFProductRepository.findByWfInstanceId(instanceId);
    if (!wfProduct.isPresent()) throw new BusinessException("Something went wrong ...");
    WFProduct currentWFProduct = wfProduct.get();

    return currentWFProduct;

  }

  @Override
  public List < WFTaskDetails > getUserTasks(long userId) {
    List < WFTaskDetails > wfTasks = wFTaskDetailsRepository.findByAssigneeId(userId);
    return wfTasks;

  }
  
  public WFTask getTaskById(long taskId) {
      Optional<WFTask> optionalTask = wFTaskRepository.findById(taskId);
      return optionalTask.orElse(null);  // Return null if task not found
  }

}