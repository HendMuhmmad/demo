package com.example.demo.service.workflow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.orm.workflow.ProductTransactionHistory;
import com.example.demo.repository.workflow.ProductTransactionHistoryRepository;

@Service
@Transactional
public class ProductTransactionHistoryServiceImpl implements ProductTransactionHistoryService {

    @Autowired
    ProductTransactionHistoryRepository productTransactionHistoryRepository;
    
	@Override
	public ProductTransactionHistory save(ProductTransactionHistory wfInstance) {
		return productTransactionHistoryRepository.save(wfInstance);
	}
	
	public ProductTransactionHistory save(Long productId) {
		return productTransactionHistoryRepository.findByProductId(productId).orElse(null);
	}
	
	

}
