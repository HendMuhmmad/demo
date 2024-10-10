package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.ProductTranHistory;
import com.example.demo.repository.ProductTranHistoryRepository;

@Service
@Transactional

public class ProductTranHistoryServiceImpl implements ProductTranHistoryService {

    @Autowired
    private ProductTranHistoryRepository productTranHistoryRepository;

    @Override
    public ProductTranHistory save(ProductTranHistory productTranHistory) {
	return productTranHistoryRepository.save(productTranHistory);
    }

    @Override
    public ProductTranHistory constructProductTransHisFromProduct(Product product) {
	if (product == null) {
	    throw new IllegalArgumentException("Product cannot be null");
	}

	return ProductTranHistory.builder()
		.productId(product.getId())
		.productName(product.getProductName())
		.price(product.getPrice())
		.color(product.getColor())
		.stockQuantity(product.getStockQuantity())
		.description(product.getDescription())
		.build();
    }
}
