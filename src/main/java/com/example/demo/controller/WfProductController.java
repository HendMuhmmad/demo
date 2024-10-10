package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.mapper.WfProductMapper;
import com.example.demo.model.dto.WfProductDto;
import com.example.demo.service.workflow.WFProductService;

@RestController
@RequestMapping("/api/Wfproduct")
public class WfProductController {

    @Autowired
    private WFProductService wfProductService;

    @PostMapping("/sendProduct")
    public void sendProduct(@RequestBody WfProductDto wfProductDto) {
	this.wfProductService.initWf(WfProductMapper.INSTANCE.mapWfProduct(wfProductDto), wfProductDto.getLoginId());
    }

    @PostMapping("/doAction")
    public ResponseEntity<String> doAction(@RequestParam Long taskId, @RequestParam Long actionId) {
	return wfProductService.doAction(taskId, actionId);
    }

}
