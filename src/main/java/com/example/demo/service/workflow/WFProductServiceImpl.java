package com.example.demo.service.workflow;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.enums.RoleEnum;
import com.example.demo.enums.WFActionEnum;
import com.example.demo.enums.workflow.SuperAdminApprovel;
import com.example.demo.enums.workflow.WFInstanceStatusEnum;
import com.example.demo.enums.workflow.WFProcessesEnum;
import com.example.demo.enums.workflow.WFProductStatusEnum;
import com.example.demo.exception.BusinessException;
import com.example.demo.mapper.WfProductMapper;
import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.WFProduct;
import com.example.demo.model.orm.workflow.WFInstance;
import com.example.demo.model.orm.workflow.WFTask;
import com.example.demo.repository.workflow.WFProductRepository;
import com.example.demo.service.ProductService;
import com.example.demo.service.ProductTranHistoryService;
import com.example.demo.service.UserService;

@Service
@Transactional

public class WFProductServiceImpl extends BaseWFServiceImpl implements WFProductService {

    @Autowired
    private WFProductRepository wfProductRepository;
    @Autowired
    private WFTaskService wfTaskService;
    @Autowired
    private WFInstanceService wfInstanceService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;
    @Autowired
    private ProductTranHistoryService productTranHistoryService;

    @Override
    public WFProduct findByWfInstanceId(Long wfInstanceId) {
	return wfProductRepository.findByWfInstanceId(wfInstanceId);
    }

    @Override
    public void init(Product product, Long loginId) throws BusinessException {
	Long instanceId = null;

	try {
	    instanceId = initWF(loginId, WFProcessesEnum.ADD_PRODUCT.getCode(), SuperAdminApprovel.SUPER_ADMIN.getCode());
	    // product.setWfStatus(WFProductStatusEnum.UNDER_APPROVE.getCode());
	    Long productId = productService.save(product, loginId);
	    WFProduct wfProduct = new WFProduct();
	    wfProduct.setProductId(productId);
	    // wfProduct.setWfInstanceId(instanceId);
	    wfProductRepository.save(wfProduct);

	} catch (Exception e) {

	    throw new BusinessException(e.getMessage());

	}
    }

    @Override
    public void initWf(WFProduct wfProduct, Long loginId) throws BusinessException {
	validateWFProduct(wfProduct);
	if (userService.getUserById(loginId).get().getRoleId() == RoleEnum.ADMIN.getCode()) {
	    Long instanceId = null;

	    try {
		instanceId = initWF(loginId, wfProduct.getStatus(), SuperAdminApprovel.SUPER_ADMIN.getCode());
		wfProduct.setWfInstanceId(instanceId);
		save(wfProduct);

	    } catch (Exception e) {

		throw new BusinessException(e.getMessage());

	    }
	} else if (userService.getUserById(loginId).get().getRoleId() == RoleEnum.SUPER_ADMIN.getCode()) {
	    productService.save(WfProductMapper.INSTANCE.maWfProductToProduct(wfProduct));
	}
    }

    public void validateWFProduct(WFProduct wfProduct) {
	if (wfProduct == null) {
	    throw new BusinessException("Product cannot be null.");
	}

	if (wfProduct.getProductName() == null || wfProduct.getProductName().isEmpty()) {
	    throw new BusinessException("Product name is required.");
	}

	if (wfProduct.getPrice() == null || wfProduct.getPrice() < 0) {
	    throw new BusinessException("Product price must be a non-negative value.");
	}

	if (wfProduct.getStockQuantity() < 0) {
	    throw new BusinessException("Stock quantity must be zero or a positive integer.");
	}

	if (wfProduct.getColor() == null || wfProduct.getColor().isEmpty()) {
	    throw new BusinessException("Product color is required.");
	}
	if (!getRunningRequests(wfProduct.getProductId()).isEmpty()) {
	    throw new BusinessException("Conflict: Workflow instance is currently running.");
	}

    }

    public WFProduct save(WFProduct wfProduct) {
	return wfProductRepository.save(wfProduct);

    }

    @Override
    public ResponseEntity<String> doAction(Long taskId, Long actionId) {
	if (taskId == null)
	    return ResponseEntity.badRequest().body("Task ID cannot be null.");
	WFTask wfTask = wfTaskService.getWFTaskById(taskId);
	if (wfTask == null)
	    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found with ID: " + taskId);
	if (wfInstanceService.getWFInstanceById(wfTask.getInstanceId()).getStatus() == WFInstanceStatusEnum.RUNNING.getCode()) {
	    wfTask.setActionDate(new Date());
	    if (actionId == WFActionEnum.APPROVE.getCode()) {
		wfTask.setActionId(WFActionEnum.APPROVE.getCode());
	    } else if (actionId == WFActionEnum.REJECTED.getCode()) {
		wfTask.setActionId(WFActionEnum.REJECTED.getCode());
		wfTask.setRefuseReasons("REJECTED");
	    }
	    wfTaskService.save(wfTask);
	    doAffect(wfTask);

	    return ResponseEntity.ok("Action " + actionId + " successfully : " + taskId);
	} else {
	    return ResponseEntity.ok("wf is not runnung: " + taskId);

	}
    }

    private void doAffect(WFTask wfTask) {
	WFInstance wfInstance = wfInstanceService.getWFInstanceById(wfTask.getInstanceId());
	wfInstance.setStatus(WFInstanceStatusEnum.DONE.getCode());
	wfInstanceService.save(wfInstance);
	WFProduct wfProduct = findByWfInstanceId(wfTask.getInstanceId());
	Product product = productService.contstructProductFromWfProduct(wfProduct);
	productService.save(product, wfTask.getAssigneeId());
	wfProduct.setProductId(product.getId());
	save(wfProduct);
	if (wfProduct.getStatus() == WFProductStatusEnum.UPDATE.getCode() || wfProduct.getStatus() == WFProductStatusEnum.DELETE.getCode())
	    productTranHistoryService.save(productTranHistoryService.constructProductTransHisFromProduct(product));
	if (wfProduct.getStatus() == WFProductStatusEnum.DELETE.getCode())
	    productService.deleteProduct(product.getId(), wfTask.getAssigneeId());
    }

    @Override
    public List<WFProduct> getRunningRequests(Long productId) {
	return wfProductRepository.getRunningRequests(productId);
    }

}
