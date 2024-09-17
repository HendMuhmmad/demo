package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.mapper.ProductMapper;
import com.example.demo.model.dto.ProductDTO;
import com.example.demo.model.dto.ProductUpdateStockQuantityDTO;
import com.example.demo.service.ProductService;

@RestController
@RequestMapping("/api/p")
public class ProductController {

    @Autowired
    public ProductService productService;

    @PostMapping("/createProduct")
    public ResponseEntity<String> createProduct(@RequestBody ProductDTO productdto) {
	return productService.save(ProductMapper.INSTANCE.mapCreateProduct(productdto), productdto.getLoginId());
    }

    @PutMapping("/updateProductStockQuantity")
    public ResponseEntity<String> updateProductStockQuantity(@RequestBody ProductUpdateStockQuantityDTO productdto) {
	return productService.updateProductQuantity(productdto.getId(), productdto.getStockQuantity(), productdto.getLoginId());
    }

    @DeleteMapping("/deleteProduct/{productId}")
    public String deleteProduct(@PathVariable int productId, @RequestParam int loginId) {
	boolean isDeleted = productService.deleteProduct(productId, loginId);
	if (isDeleted) {
	    return "Product deleted successfully";
	} else {
	    return "Product deletion failed-Unautherized";
	}
    }

}
