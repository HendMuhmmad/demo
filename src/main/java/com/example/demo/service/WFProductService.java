package com.example.demo.service;

import java.util.List;

import com.example.demo.model.dto.TaskRequestDto;
import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.WFProduct;
import com.example.demo.model.orm.workflow.WFTaskDetails;

public interface WFProductService {

	public void initWFProduct(Product theProduct, long requesterId , long operation);
	
	public void respondToRequest(TaskRequestDto taskRequest);
	
//	public void doAction(WFProduct wfProduct);
	
	public List<WFTaskDetails> getTasksByUserId(long userId);
	
	public WFTaskDetails getTaskByTaskId(long taskId);
	
}
