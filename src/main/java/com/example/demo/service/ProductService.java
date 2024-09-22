package com.example.demo.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.example.demo.model.orm.Product;

public interface ProductService {
    public Product findbyId(int theId);

    public ResponseEntity<String> save(Product theProduct, int loginId);

    public ResponseEntity<String> deleteProduct(int productId, int loginId);

    public ResponseEntity<String> updateProductQuantity(int productId, int newQuantity, int loginId);

    public List<Product> getAllProduct();
}
