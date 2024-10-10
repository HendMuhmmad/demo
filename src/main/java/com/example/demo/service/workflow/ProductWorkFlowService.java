package com.example.demo.service.workflow;

import java.util.List;

import com.example.demo.model.dto.workflow.WFTaskDto;
import com.example.demo.model.orm.workflow.product.WFProduct;

public interface ProductWorkFlowService {

	void initProductWorkFlow(Long processId,WFProduct wfProduct,Long loginId);

	void doTaskAction(Long actionId, Long taskId, String notes, String refuseNotes, Long loginId);

	List<WFTaskDto<WFProduct>> getProductWorkFlowTasksByAsigneeId(Long asigneeId);

	WFTaskDto<WFProduct> getProductWorkFlowTaskById(Long taskId);

}
