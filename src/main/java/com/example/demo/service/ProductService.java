package com.example.demo.service;

import java.util.List;

import com.example.demo.exception.BusinessException;
import com.example.demo.model.orm.Product;

public interface ProductService {
    public Product findbyId(Long theId);

    public Long save(Product theProduct, Long loginId);

    public void deleteProduct(Long productId, Long loginId);

    public void updateProductQuantity(Long productId, int newQuantity, Long loginId);

    public List<Product> getAllProduct();
//    public void changeWFProductRequestStatus(Integer taskId, Long userId, boolean isApproved);
    public void updateProductQuantityWithOutAuth(Long productId, int newQuantity) throws BusinessException;
//    public boolean requestCreateProduct(Product theProduct, Long loginId);
//    public boolean requestUpdateProduct(Product theProduct, Long loginId);
//    public boolean requestDeleteProduct(Product theProduct, Long loginId);
}
