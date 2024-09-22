package com.example.demo.service;

import java.util.List;

import com.example.demo.model.orm.Product;

public interface ProductService {
    public Product findbyId(int theId);

    public int save(Product theProduct, int loginId);

    public void deleteProduct(int productId, int loginId);

    public void updateProductQuantity(int productId, int newQuantity, int loginId);

    public List<Product> getAllProduct();
}
