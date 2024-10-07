package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.dto.TaskRequestDto;
import com.example.demo.model.orm.workflow.WFTaskDetails;
import com.example.demo.service.ProductService;
import com.example.demo.service.WFProductService;

@RestController
@RequestMapping("/api/task")
public class TaskController {
	
	@Autowired
    public ProductService productService;
    
    @Autowired
    public WFProductService wfProductService;

    @GetMapping("/tasks")
    public ResponseEntity<List<WFTaskDetails>> getProductWorkflowTasks(@RequestParam long userId) {
        List<WFTaskDetails> tasks = wfProductService.getTasksByUserId(userId);
        return ResponseEntity.ok(tasks);
    }
    

    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<WFTaskDetails> getTaskByTaskId(@PathVariable long taskId) {
        WFTaskDetails task = wfProductService.getTaskByTaskId(taskId);
        return ResponseEntity.ok(task);
    }

    
    @PostMapping("/tasks/respondToRequest")
    public ResponseEntity<String> respondToRequest(@RequestBody TaskRequestDto taskRequest) {
        productService.respondToRequest(taskRequest); 
        return ResponseEntity.ok("{\"message\":\"The task is finished successfully\"}");
    }
}
