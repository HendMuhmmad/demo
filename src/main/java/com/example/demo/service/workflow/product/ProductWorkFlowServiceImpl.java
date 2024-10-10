package com.example.demo.service.workflow.product;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.enums.ProductStatusEnum;
import com.example.demo.enums.workflow.WFActionEnum;
import com.example.demo.enums.workflow.WFProcessesEnum;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.dto.workflow.WFTaskDto;
import com.example.demo.model.orm.workflow.WFInstance;
import com.example.demo.model.orm.workflow.WFTask;
import com.example.demo.model.orm.workflow.product.WFProduct;
import com.example.demo.repository.workflow.product.WFProductRepository;
import com.example.demo.service.ProductService;
import com.example.demo.service.UtilsService;
import com.example.demo.service.workflow.WorkFlowBaseService;

@Service
@Transactional
public class ProductWorkFlowServiceImpl implements ProductWorkFlowService {



	@Autowired
	private WFProductRepository wfProductRepository;

	@Autowired
	private ProductService productService;
	
	@Autowired
	private WorkFlowBaseService workFlowBaseService;
	
	@Autowired
	private UtilsService utilsService;


	@Override
	public void initProductWorkFlow(Long processId, WFProduct wfProduct, Long loginId) {
		validateInitWorkFlowData(processId, loginId, wfProduct);
		WFInstance wfInstance = workFlowBaseService.createAndSaveWFInstance(processId, loginId);
		updateWFProduct(processId, wfProduct, wfInstance.getId());
		workFlowBaseService.assignTaskToSuperAdmin(wfInstance.getId());
	}

	@Override
	public void doTaskAction(Long actionId, Long taskId, String notes, String refuseNotes, Long loginId) {
		validateTaskActionInputs(actionId, taskId);

		WFTask wfTask = workFlowBaseService.getTaskById(taskId);
		workFlowBaseService.validateTaskOwnership(wfTask, loginId);

		WFInstance wfInstance = workFlowBaseService.getWFInstanceById(wfTask.getInstanceId());
		workFlowBaseService.validateWorkflowState(wfInstance);

		WFProduct wfProduct = getProductByInstanceId(wfInstance.getId());
		workFlowBaseService.validateTaskNotAlreadyActedUpon(wfTask);

		performAction(actionId, wfTask, wfInstance, wfProduct, notes, refuseNotes);
	}

	@Override
	public List<WFTaskDto<WFProduct>> getProductWorkFlowTasksByAsigneeId(Long assigneeId) {
		return workFlowBaseService.findByAssigneeIdAndActionIdIsNull(assigneeId).stream().map(this::createWFTaskDto)
				.collect(Collectors.toList());
	}

	@Override
	public WFTaskDto<WFProduct> getProductWorkFlowTaskById(Long taskId) {
		utilsService.validateNotNull(taskId, "Task ID");
		return createWFTaskDto(workFlowBaseService.getTaskById(taskId));
	}



	private void performAction(Long actionId, WFTask wfTask, WFInstance wfInstance, WFProduct wfProduct, String notes,
			String refuseNotes) {
		workFlowBaseService.updateWorkflowToDone(wfInstance);

		if (actionId.equals(WFActionEnum.APPROVE.getCode())) {
			approveTask(wfTask, wfProduct, notes);
		} else if (actionId.equals(WFActionEnum.REJECT.getCode())) {
			workFlowBaseService.rejectTask(wfTask, refuseNotes, notes);
		}
	}

	private void approveTask(WFTask wfTask, WFProduct wfProduct, String notes) {
		workFlowBaseService.approveTask(wfTask, notes);
		productService.doApproveEffect(wfProduct);
	}



	private void updateWFProduct(Long processId, WFProduct wfProduct, Long wfInstanceId) {
		checkRunningWorkFlowRequests(wfProduct, wfInstanceId);
		wfProduct.setWfInstanceId(wfInstanceId);
		setProductStatusBasedOnProcess(wfProduct, processId);
		wfProductRepository.save(wfProduct);
	}

	private void setProductStatusBasedOnProcess(WFProduct wfProduct, Long processId) {
		if (processId.equals(WFProcessesEnum.ADD_PRODUCT.getCode())) {
			wfProduct.setStatus(ProductStatusEnum.ADD.getCode());
		} else if (processId.equals(WFProcessesEnum.UPDATE_PRODUCT.getCode())) {
			wfProduct.setStatus(ProductStatusEnum.UPDATE.getCode());
		} else if (processId.equals(WFProcessesEnum.DELETE_PRODUCT.getCode())) {
			wfProduct.setStatus(ProductStatusEnum.DELETE.getCode());
		}
	}

	private WFTaskDto<WFProduct> createWFTaskDto(WFTask wfTask) {
		WFInstance wfInstance = workFlowBaseService.getWFInstanceById(wfTask.getInstanceId());
		WFProduct wfProduct = getProductByInstanceId(wfInstance.getId());

		return new WFTaskDto<>(wfTask.getId(), wfTask.getInstanceId(), wfTask.getAssigneeId(), wfTask.getAssignDate(),
				wfTask.getAssigneeRole(), wfTask.getNotes(), wfTask.getRefuseReasons(), wfProduct);
	}


	private void validateInitWorkFlowData(Long processId, Long loginId, WFProduct wfProduct) {
		utilsService.validateNotNull(processId, "Process ID");
		utilsService.validateNotNull(loginId, "Login ID");
		utilsService.validateNotNull(wfProduct, "Product");
	}

	private void validateTaskActionInputs(Long actionId, Long taskId) {
		utilsService.validateNotNull(actionId, "Action ID");
		utilsService.validateNotNull(taskId, "Task ID");
	}



	private void checkRunningWorkFlowRequests(WFProduct wfProduct, Long wfInstanceId) {
		if (!wfProductRepository.checkRunningWorkFlowRequests(wfProduct.getProductId(), wfInstanceId).isEmpty()) {
			throw new BusinessException("Product is already attached to another workflow instance");
		}
	}

	private WFProduct getProductByInstanceId(Long instanceId) {
		return wfProductRepository.findByWfInstanceId(instanceId)
				.orElseThrow(() -> new BusinessException("WFProduct not found"));
	}

}
