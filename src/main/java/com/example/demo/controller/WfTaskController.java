package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.dto.workflow.WFTaskDetailsDto;
import com.example.demo.service.workflow.WFTaskService;

@RestController
@RequestMapping("/tasks")

public class WfTaskController {

    @Autowired
    private WFTaskService wfTaskService;

    @GetMapping("/getProductWfTasks")
    public List<WFTaskDetailsDto<?>> getProductWfTasks(@RequestParam Long userId) {
	return wfTaskService.getProductWfTasks(userId);
    }

    @GetMapping("/getTaskById")
    public WFTaskDetailsDto<?> getTaskById(@RequestParam Long taskId) {
	return wfTaskService.getTaskById(taskId);
    }

    // @PostMapping("/TakeAction")
    // public ResponseEntity<String> takeAction(@RequestParam Long taskId, @RequestParam Long actionId) {
    // return wfTaskService.tackAction(taskId, actionId);
    // }
    //
}
