package com.example.demo.service;

import java.util.List;

import com.example.demo.exception.BusinessException;
import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.workflow.product.WFProduct;

public interface ProductService {
    public Product findById(Long productId);

    public void save(WFProduct wfProduct, Long loginId);

    public void deleteProduct(Long productId, Long loginId);

    public List<Product> getAllProducts();

	public void doApproveEffect(WFProduct wfProduct);
	
    public void updateProductQuantityWithoutAuth(Long productId, int newQuantity) throws BusinessException;


}
