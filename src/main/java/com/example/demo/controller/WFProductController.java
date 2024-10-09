package com.example.demo.controller;

import java.util.List;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.dto.workflow.ResponseDto;
import com.example.demo.model.orm.workflow.WFTaskDetails;
import com.example.demo.service.ProductService;
import com.example.demo.service.WFProductService;

@RestController
@RequestMapping("/api/product/tasks")
public class WFProductController {

    @Autowired
    public ProductService productService;
    
    @Autowired
    public WFProductService wfProductService;
     @GetMapping("/getTasksByAssigneeId")
     public ResponseEntity<List<WFTaskDetails>> getProductWorkflowTasksByAssigneeId(@RequestParam Long assigneeId) {
     List<WFTaskDetails> tasks = wfProductService.getTasksByAssigneeId(assigneeId);
     return ResponseEntity.ok(tasks); 
     }
    
     @GetMapping("/getTaskByTaskId")
     public ResponseEntity<WFTaskDetails> getProductWorkflowTasksByTaskId(@RequestParam Long taskId) {
     WFTaskDetails task = wfProductService.getTaskByTaskId(taskId);
     return ResponseEntity.ok(task); 
     }

    @PostMapping("/respondToRequest")
    public ResponseEntity<String> respondToRequest(@RequestBody ResponseDto responseDto) {
        wfProductService.respondToTask(responseDto.getTaskId(), responseDto.getLoginId(), responseDto.getResponse(),responseDto.getRejectionReason(), responseDto.getNote());
        return ResponseEntity.ok("{\"message\":\"Response registered Successfully\"}");
    }
}
