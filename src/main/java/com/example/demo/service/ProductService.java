package com.example.demo.service;

import java.util.List;

import com.example.demo.exception.BusinessException;
import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.WFProduct;

public interface ProductService {
    public Product findbyId(Long theId);

    public Long save(Product theProduct, Long loginId);

    public Product save(Product product);

    public void deleteProduct(Long productId, Long loginId);

    public void updateProductQuantity(Long productId, int newQuantity, Long loginId);

    public List<Product> getAllProduct();

    public void updateProductQuantityWithOutAuth(Long productId, int newQuantity) throws BusinessException;

    public Product contstructProductFromWfProduct(WFProduct wfProduct);

}
