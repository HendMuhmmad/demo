package com.example.demo.service;

import org.springframework.http.ResponseEntity;

import com.example.demo.model.orm.Product;

public interface ProductService {
    public Product findbyId(int theId);

    public ResponseEntity<String> save(Product theProduct, int loginId);

    public boolean deleteProduct(int productId, int loginId);

    public ResponseEntity<String> updateProductQuantity(int productId, int newQuantity, int loginId);

}
