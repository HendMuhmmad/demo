package com.example.demo.service.workflow;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.enums.ProductStatusEnum;
import com.example.demo.enums.RoleEnum;
import com.example.demo.enums.workflow.WFActionEnum;
import com.example.demo.enums.workflow.WFAsigneeRoleEnum;
import com.example.demo.enums.workflow.WFInstanceStatusEnum;
import com.example.demo.enums.workflow.WFProcessesEnum;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.dto.workflow.WFTaskDto;
import com.example.demo.model.orm.User;
import com.example.demo.model.orm.workflow.WFInstance;
import com.example.demo.model.orm.workflow.WFTask;
import com.example.demo.model.orm.workflow.product.WFProduct;
import com.example.demo.repository.workflow.WFInstanceRepository;
import com.example.demo.repository.workflow.WFTaskRepository;
import com.example.demo.repository.workflow.product.WFProductRepository;
import com.example.demo.service.ProductService;
import com.example.demo.service.UserService;

@Service
@Transactional
public class ProductWorkFlowServiceImpl implements ProductWorkFlowService {

	@Autowired
	private WFInstanceRepository wfInstanceRepository;

	@Autowired
	private WFTaskRepository wfTaskRepository;

	@Autowired
	private WFProductRepository wfProductRepository;

	@Autowired
	private ProductService productService;

	@Autowired
	private UserService userService;

	@Override
	public void initProductWorkFlow(Long processId, WFProduct wfProduct, Long loginId) {
		validateInitWorkFlowData(processId, loginId, wfProduct);
		WFInstance wfInstance = createAndSaveWFInstance(processId, loginId);
		updateWFProduct(processId, wfProduct, wfInstance.getId());
		assignTaskToSuperAdmin(wfInstance.getId());
	}

	@Override
	public void doTaskAction(Long actionId, Long taskId, String notes, String refuseNotes, Long loginId) {
		validateTaskActionInputs(actionId, taskId);

		WFTask wfTask = getTaskById(taskId);
		validateTaskOwnership(wfTask, loginId);

		WFInstance wfInstance = getWFInstanceById(wfTask.getInstanceId());
		validateWorkflowState(wfInstance);

		WFProduct wfProduct = getProductByInstanceId(wfInstance.getId());
		validateTaskNotAlreadyActedUpon(wfTask);

		performAction(actionId, wfTask, wfInstance, wfProduct, notes, refuseNotes);
	}

	@Override
	public List<WFTaskDto<WFProduct>> getProductWorkFlowTasksByAsigneeId(Long assigneeId) {
		return wfTaskRepository.findByAssigneeIdAndActionIdIsNull(assigneeId).stream().map(this::createWFTaskDto)
				.collect(Collectors.toList());
	}

	@Override
	public WFTaskDto<WFProduct> getProductWorkFlowTaskById(Long taskId) {
		validateNotNull(taskId, "Task ID");
		return createWFTaskDto(getTaskById(taskId));
	}

	private void assignTaskToSuperAdmin(Long instanceId) {
		List<User> superAdmins = userService.getUserByRoleId(RoleEnum.SUPER_ADMIN.getCode());
		if (superAdmins.isEmpty()) {
			throw new BusinessException("No Super Admins were found");
		}
		saveTask(createWFTask(instanceId, superAdmins.get(0).getId()));
	}

	private WFInstance createAndSaveWFInstance(Long processId, Long loginId) {
		WFInstance wfInstance = new WFInstance();
		wfInstance.setProcessId(processId);
		wfInstance.setRequesterId(loginId);
		wfInstance.setStatus(WFInstanceStatusEnum.RUNNING.getCode());
		wfInstance.setRequestDate(new Date());
		return wfInstanceRepository.save(wfInstance);
	}

	private WFTask createWFTask(Long instanceId, Long assigneeId) {
		WFTask wfTask = new WFTask();
		wfTask.setInstanceId(instanceId);
		wfTask.setAssigneeId(assigneeId);
		wfTask.setAssigneeRole(WFAsigneeRoleEnum.PRODUCT_MANAGER.getCode());
		wfTask.setAssignDate(new Date());
		return wfTask;
	}

	private void performAction(Long actionId, WFTask wfTask, WFInstance wfInstance, WFProduct wfProduct, String notes,
			String refuseNotes) {
		updateWorkflowToDone(wfInstance);

		if (actionId.equals(WFActionEnum.APPROVE.getCode())) {
			approveTask(wfTask, wfProduct, notes);
		} else if (actionId.equals(WFActionEnum.REJECT.getCode())) {
			rejectTask(wfTask, refuseNotes, notes);
		}
	}

	private void approveTask(WFTask wfTask, WFProduct wfProduct, String notes) {
		wfTask.setActionId(WFActionEnum.APPROVE.getCode());
		wfTask.setNotes(notes);
		saveTask(wfTask);
		productService.doApproveEffect(wfProduct);
	}

	private void rejectTask(WFTask wfTask, String refuseNotes, String notes) {
		validateRefuseNotes(refuseNotes);
		wfTask.setActionId(WFActionEnum.REJECT.getCode());
		wfTask.setRefuseReasons(refuseNotes);
		wfTask.setNotes(notes);
		saveTask(wfTask);
	}

	private void updateWorkflowToDone(WFInstance wfInstance) {
		wfInstance.setStatus(WFInstanceStatusEnum.DONE.getCode());
		wfInstanceRepository.save(wfInstance);
	}

	private void updateWFProduct(Long processId, WFProduct wfProduct, Long wfInstanceId) {
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
		WFInstance wfInstance = getWFInstanceById(wfTask.getInstanceId());
		WFProduct wfProduct = getProductByInstanceId(wfInstance.getId());

		return new WFTaskDto<>(wfTask.getId(), wfTask.getInstanceId(), wfTask.getAssigneeId(), wfTask.getAssignDate(),
				wfTask.getAssigneeRole(), wfTask.getNotes(), wfTask.getRefuseReasons(), wfProduct);
	}


	private void validateInitWorkFlowData(Long processId, Long loginId, WFProduct wfProduct) {
		validateNotNull(processId, "Process ID");
		validateNotNull(loginId, "Login ID");
		validateNotNull(wfProduct, "Product");
	}

	private void validateTaskActionInputs(Long actionId, Long taskId) {
		validateNotNull(actionId, "Action ID");
		validateNotNull(taskId, "Task ID");
	}

	private void validateTaskOwnership(WFTask wfTask, Long loginId) {
		if (!wfTask.getAssigneeId().equals(loginId)) {
			throw new BusinessException("You are not the owner of this task");
		}
	}

	private void validateWorkflowState(WFInstance wfInstance) {
		if (!wfInstance.getStatus().equals(WFInstanceStatusEnum.RUNNING.getCode())) {
			throw new BusinessException("Workflow is not in a valid state for this action");
		}
	}

	private void validateTaskNotAlreadyActedUpon(WFTask wfTask) {
		if (wfTask.getActionId() != null) {
			throw new BusinessException("Task has already been acted upon");
		}
	}

	private void validateRefuseNotes(String refuseNotes) {
		validateNotNullOrEmpty(refuseNotes, "Refuse Notes");
	}

	private WFTask getTaskById(Long taskId) {
		return wfTaskRepository.findById(taskId).orElseThrow(() -> new BusinessException("WFTask not found"));
	}

	private WFInstance getWFInstanceById(Long instanceId) {
		return wfInstanceRepository.findById(instanceId)
				.orElseThrow(() -> new BusinessException("WFInstance not found"));
	}

	private WFProduct getProductByInstanceId(Long instanceId) {
		return wfProductRepository.findByWfInstanceId(instanceId)
				.orElseThrow(() -> new BusinessException("WFProduct not found"));
	}

	private void validateNotNull(Object object, String paramName) {
		if (object == null) {
			throw new BusinessException(paramName + " cannot be null");
		}
	}

	private void validateNotNullOrEmpty(String str, String paramName) {
		if (str == null || str.trim().isEmpty()) {
			throw new BusinessException(paramName + " cannot be null or empty");
		}
	}

	private void saveTask(WFTask wfTask) {
		wfTaskRepository.save(wfTask);
	}
}
