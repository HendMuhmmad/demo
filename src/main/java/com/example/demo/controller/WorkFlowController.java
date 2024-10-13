package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.mapper.ProductMapper;
import com.example.demo.model.dto.ProductDto;
import com.example.demo.model.dto.ProductUpdateDto;
import com.example.demo.model.dto.TaskRequestDto;
import com.example.demo.model.orm.workflow.WFTask;
import com.example.demo.model.orm.workflow.WFTaskDetails;
import com.example.demo.service.WFProductService;

@RestController
@RequestMapping("/api/workflow")
public class WorkFlowController {

    
    @Autowired
    public WFProductService wfProductService;

 

    @PostMapping("/tasks/respondTask")
    public ResponseEntity<String> changeRequestStatus(
        @RequestBody TaskRequestDto taskRequest) {
//        @RequestParam Integer taskId,
//        @RequestParam Long productId,
//        @RequestParam Long userId,
//        @RequestParam boolean isApproved
    	
    	wfProductService.changeRequestStatus(taskRequest.getTaskId(), taskRequest.getUserId(), taskRequest.getIsApproved());
 
        return ResponseEntity.ok("{\"message\":\"Request Status Changed Successfully\"}");
    }
 

    
    @PostMapping("/product/requestCreation")
    public ResponseEntity<String> createProduct(@RequestBody ProductDto productdto) {
 
 
      wfProductService.addWFProduct(productdto.getLoginId(), ProductMapper.INSTANCE.mapCreateProduct(productdto));
	return ResponseEntity.ok("Product created successfully   ");
    }
    
    @PutMapping("/updateProduct")
    public ResponseEntity<String> updateProduct(@RequestBody ProductUpdateDto productUpdateDto) {
      long taskId =   wfProductService.updateWFProduct(productUpdateDto.getLoginId(), productUpdateDto.getId(),ProductMapper.INSTANCE.mapUpdateProduct(productUpdateDto));
	return ResponseEntity.ok("Product updated successfully + with task Id = " + taskId);

    }
    
    
    @DeleteMapping("/deleteProduct/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId, @RequestParam Long loginId) {
    long taskId = wfProductService.deleteWFProduct(loginId, productId);
	return ResponseEntity.ok("Product deleted successfully + with task Id = " + taskId);
    }
    // @GetMapping("/tasks")
    // public ResponseEntity<List<ProductWorkflowTask>> getProductWorkflowTasks(@RequestParam Integer userId) {
    // List<ProductWorkflowTask> tasks = productWorkflowService.getTasksByUserId(userId);
    // return ResponseEntity.ok(tasks);
    // }

    @PostMapping("/tasks/approve")
    public ResponseEntity<String> approve(@RequestParam Integer taskId) {
	// productWorkflowService.approveTask(taskId);
	return ResponseEntity.ok("{\"message\":\"Approved Successfully\"}");
    }

    @PostMapping("/tasks/reject")
    public ResponseEntity<String> rejectProductRequest(@RequestParam Integer taskId) {
	// productWorkflowService.rejectTask(taskId);
	return ResponseEntity.ok("{\"message\":\"Rejected Successfully\"}");
    }
    
    @GetMapping("/tasks")
    public ResponseEntity<List<WFTaskDetails>> getProductWorkflowTasks(@RequestParam long userId) {
        List<WFTaskDetails> tasks = wfProductService.getUserTasks(userId);
        return ResponseEntity.ok(tasks);
    }
    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<WFTask> getTaskById(@PathVariable long taskId) {
    	WFTask task = wfProductService.getTaskById(taskId);
    	if (task == null) {
    	return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    	}
    	return ResponseEntity.ok(task);
    }
}
