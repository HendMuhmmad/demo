package com.example.demo.service.workflow;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.enums.RoleEnum;
import com.example.demo.enums.workflow.WFActionEnum;
import com.example.demo.enums.workflow.WFAsigneeRoleEnum;
import com.example.demo.enums.workflow.WFInstanceStatusEnum;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.orm.User;
import com.example.demo.model.orm.workflow.WFInstance;
import com.example.demo.model.orm.workflow.WFTask;
import com.example.demo.repository.workflow.WFInstanceRepository;
import com.example.demo.repository.workflow.WFTaskRepository;
import com.example.demo.service.UserService;
import com.example.demo.service.UtilsService;

@Service
@Transactional
public class WorkFlowBaseServiceImpl implements WorkFlowBaseService {
	
	@Autowired
	private WFInstanceRepository wfInstanceRepository;

	@Autowired
	private WFTaskRepository wfTaskRepository;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UtilsService utilsService;
	
	@Override
	public void assignTaskToSuperAdmin(Long instanceId) {
		List<User> superAdmins = userService.getUserByRoleId(RoleEnum.SUPER_ADMIN.getCode());
		if (superAdmins.isEmpty()) {
			throw new BusinessException("No Super Admins were found");
		}
		wfTaskRepository.save(createWFTask(instanceId, superAdmins.get(0).getId()));
	}

	@Override
	public WFInstance createAndSaveWFInstance(Long processId, Long loginId) {
		WFInstance wfInstance = new WFInstance();
		wfInstance.setProcessId(processId);
		wfInstance.setRequesterId(loginId);
		wfInstance.setStatus(WFInstanceStatusEnum.RUNNING.getCode());
		wfInstance.setRequestDate(new Date());
		return wfInstanceRepository.save(wfInstance);
	}

	@Override
	public WFTask createWFTask(Long instanceId, Long assigneeId) {
		WFTask wfTask = new WFTask();
		wfTask.setInstanceId(instanceId);
		wfTask.setAssigneeId(assigneeId);
		wfTask.setAssigneeRole(WFAsigneeRoleEnum.PRODUCT_MANAGER.getCode());
		wfTask.setAssignDate(new Date());
		return wfTask;
	}
	
	@Override
	public void rejectTask(WFTask wfTask, String refuseNotes, String notes) {
		validateRefuseNotes(refuseNotes);
		wfTask.setActionId(WFActionEnum.REJECT.getCode());
		wfTask.setRefuseReasons(refuseNotes);
		wfTask.setNotes(notes);
		wfTaskRepository.save(wfTask);
	}

	@Override
	public void updateWorkflowToDone(WFInstance wfInstance) {
		wfInstance.setStatus(WFInstanceStatusEnum.DONE.getCode());
		wfInstanceRepository.save(wfInstance);
	}
	
	@Override
	public void validateTaskOwnership(WFTask wfTask, Long loginId) {
		if (!wfTask.getAssigneeId().equals(loginId)) {
			throw new BusinessException("You are not the owner of this task");
		}
	}

	@Override
	public void validateWorkflowState(WFInstance wfInstance) {
		if (!wfInstance.getStatus().equals(WFInstanceStatusEnum.RUNNING.getCode())) {
			throw new BusinessException("Workflow is not in a valid state for this action");
		}
	}

	@Override
	public void validateTaskNotAlreadyActedUpon(WFTask wfTask) {
		if (wfTask.getActionId() != null) {
			throw new BusinessException("Task has already been acted upon");
		}
	}

	@Override
	public void validateRefuseNotes(String refuseNotes) {
		utilsService.validateNotNullOrEmpty(refuseNotes, "Refuse Notes");
	}
	
	@Override
	public WFTask getTaskById(Long taskId) {
		return wfTaskRepository.findById(taskId).orElseThrow(() -> new BusinessException("WFTask not found"));
	}

	@Override
	public WFInstance getWFInstanceById(Long instanceId) {
		return wfInstanceRepository.findById(instanceId)
				.orElseThrow(() -> new BusinessException("WFInstance not found"));
	}
	
	@Override
	public void approveTask(WFTask wfTask, String notes) {
		wfTask.setActionId(WFActionEnum.APPROVE.getCode());
		wfTask.setNotes(notes);
		wfTaskRepository.save(wfTask);
	}

	@Override
	public List<WFTask> findByAssigneeIdAndActionIdIsNull(Long assigneeId) {
		return wfTaskRepository.findByAssigneeIdAndActionIdIsNull(assigneeId);
	}
}
