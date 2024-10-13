package com.example.demo.service;

import java.util.List;

import com.example.demo.enums.WFProductActionStatusEnum;
import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.workflow.WFTask;
import com.example.demo.model.orm.workflow.WFTaskDetails;

public interface WFProductService {
//	public void initWFProduct(long PROCESS_ID, long requesterId,Long oldProductId, Product product, WFProductActionStatusEnum actionStatus);
	public Long addWFProduct(long requesterId, Product product);
	public Long updateWFProduct(long requesterId,long oldProductId, Product product);
	public Long deleteWFProduct(long requesterId,long oldProductId);
	public void changeRequestStatus(long taskId, long userId, boolean isApproved);
	public long getProductIdByTaskId(long taskId);
	public List<WFTaskDetails> getUserTasks(long userId);
	public WFTask getTaskById(long taskId);
}
