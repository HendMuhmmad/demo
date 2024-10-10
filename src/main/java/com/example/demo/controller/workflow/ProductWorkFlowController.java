package com.example.demo.controller.workflow;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.dto.workflow.WFTaskActionDto;
import com.example.demo.model.dto.workflow.WFTaskDto;
import com.example.demo.model.orm.workflow.product.WFProduct;
import com.example.demo.service.workflow.product.ProductWorkFlowService;


@RestController
@RequestMapping("/api/product/workflow")
public class ProductWorkFlowController {
	@Autowired
	ProductWorkFlowService productWorkFlowService;
	
	@PostMapping("/doAction")
	public ResponseEntity<Void> handleTaskAction(@RequestBody WFTaskActionDto taskActionDto) {
	    productWorkFlowService.doTaskAction(taskActionDto.getActionId(),taskActionDto.getTaskId(), taskActionDto.getNotes(), taskActionDto.getRefuseNotes(),taskActionDto.getLoginId());
	    return ResponseEntity.ok().build();
	}

    @GetMapping("/tasks/{assigneeId}")
    public ResponseEntity<List<WFTaskDto<WFProduct>>> getTasksByAssigneeId(@PathVariable Long assigneeId) {
        List<WFTaskDto<WFProduct>> tasks = productWorkFlowService.getProductWorkFlowTasksByAsigneeId(assigneeId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<WFTaskDto<WFProduct>> getTaskById(@PathVariable Long taskId) {
        WFTaskDto<WFProduct> taskDto = productWorkFlowService.getProductWorkFlowTaskById(taskId);
        return ResponseEntity.ok(taskDto);
    }

}
