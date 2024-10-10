package com.example.demo.service.workflow;

import java.util.List;

import com.example.demo.model.orm.workflow.WFInstance;
import com.example.demo.model.orm.workflow.WFTask;

public interface WorkFlowBaseService {

	void assignTaskToSuperAdmin(Long instanceId);

	WFInstance createAndSaveWFInstance(Long processId, Long loginId);

	WFTask createWFTask(Long instanceId, Long assigneeId);

	void rejectTask(WFTask wfTask, String refuseNotes, String notes);

	void updateWorkflowToDone(WFInstance wfInstance);

	void validateTaskNotAlreadyActedUpon(WFTask wfTask);

	void validateTaskOwnership(WFTask wfTask, Long loginId);

	void validateWorkflowState(WFInstance wfInstance);

	void validateRefuseNotes(String refuseNotes);

	WFInstance getWFInstanceById(Long instanceId);

	void approveTask(WFTask wfTask, String notes);

	WFTask getTaskById(Long taskId);

	List<WFTask> findByAssigneeIdAndActionIdIsNull(Long assigneeId);

}
