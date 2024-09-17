package com.example.demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.enums.RoleEnum;
import com.example.demo.model.orm.Product;
import com.example.demo.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    public ProductRepository productRepository;

    @Override
    public Product findbyId(int theId) {
	Optional<Product> product = productRepository.findById(theId);
	if (!product.isPresent())
	    throw new RuntimeException("can not find product in DB");
	return product.get();

    }

    public ResponseEntity<String> save(Product theProduct, int loginId) {
	if (loginId == RoleEnum.SUPER_ADMIN.getCode() || loginId == RoleEnum.ADMIN.getCode()) {
	    Product product = productRepository.save(theProduct);
	    // Return a success response with the product ID
	    return new ResponseEntity<>("ID=" + product.getId() + "\nProduct added successfully", HttpStatus.CREATED);
	} else {
	    // Return an error response indicating unauthorized access
	    return new ResponseEntity<>("Product addition failed - Unauthorized", HttpStatus.UNAUTHORIZED);
	}
    }

    public ResponseEntity<String> updateProductQuantity(int productId, int newQuantity, int loginId) {
	if (loginId == RoleEnum.SUPER_ADMIN.getCode() || loginId == RoleEnum.ADMIN.getCode()) {
	    Product product = productRepository.findById(productId).orElse(null);

	    if (product != null) {
		// Update product stock quantity
		product.setStockQuantity(newQuantity);
		productRepository.save(product);
		return new ResponseEntity<>("Product quantity updated successfully.", HttpStatus.OK);
	    } else {
		return new ResponseEntity<>("Product not found.", HttpStatus.NOT_FOUND);
	    }
	} else {
	    return new ResponseEntity<>("Unauthorized to perform this action.", HttpStatus.UNAUTHORIZED);
	}
    }

    @Override
    public boolean deleteProduct(int productId, int loginId) {
	if (productRepository.findById(productId).isEmpty())
	    throw new RuntimeException("can not find product in DB");
	if (loginId == RoleEnum.SUPER_ADMIN.getCode() || loginId == RoleEnum.ADMIN.getCode()) {
	    productRepository.deleteById(productId);
	    return true;
	}
	return false;
    }

}
