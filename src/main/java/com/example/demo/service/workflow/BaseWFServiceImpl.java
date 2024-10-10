package com.example.demo.service.workflow;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.enums.workflow.WFInstanceStatusEnum;
import com.example.demo.enums.workflow.WFProcessesEnum;
import com.example.demo.enums.workflow.WFProductStatusEnum;
import com.example.demo.enums.workflow.WfAssigneeRole;
import com.example.demo.model.orm.workflow.WFInstance;
import com.example.demo.model.orm.workflow.WFProcess;
import com.example.demo.model.orm.workflow.WFTask;

public class BaseWFServiceImpl implements BaseWFService {

    @Autowired
    private WFProcessService wfProcessService;

    @Autowired
    private WFInstanceService wfInstanceService;

    @Autowired
    private WFTaskService wfTaskService;

    // /*********************** WFProcess ****************************************/

    @Override
    public WFProcess getWFProcessById(long id) {
	return wfProcessService.getWFProcessById(id);
    }

    @Override
    public WFInstance getWFInstanceById(long id) {
	return wfInstanceService.getWFInstanceById(id);
    }

    @Override
    public WFTask getWFTaskById(long id) {
	return wfTaskService.getWFTaskById(id);
    }

    /*---------------------- Dynamic WF ---------------------------------------*/

    @Override
    public Long initWF(Long loginId, Long status, Long assigneeId) {

	WFInstance wfInstance = new WFInstance();

	wfInstance.setProcessId(getProcessId(status));
	wfInstance.setRequestDate(new Date());
	wfInstance.setRequesterId(loginId);
	wfInstance.setStatus(WFInstanceStatusEnum.RUNNING.getCode());
	Long instanceId = wfInstanceService.save(wfInstance);

	WFTask wfTask = new WFTask();
	wfTask.setInstanceId(instanceId);
	wfTask.setAssigneeId(assigneeId);
	wfTask.setAssigneeRole(WfAssigneeRole.SUPER_ADMIN.getCode());
	wfTask.setAssignDate(new Date());
	wfTaskService.save(wfTask);
	return instanceId;
    }

    private Long getProcessId(Long status) {
	if (status != null) {
	    if (status.equals(WFProductStatusEnum.ADD.getCode())) {
		return WFProcessesEnum.ADD_PRODUCT.getCode();
	    } else if (status.equals(WFProductStatusEnum.UPDATE.getCode())) {
		return WFProcessesEnum.UPDATE_PRODUCT.getCode();
	    } else if (status.equals(WFProductStatusEnum.DELETE.getCode())) {
		return WFProcessesEnum.DELETE_PRODUCT.getCode();
	    }
	}

	return null;
    }
}
