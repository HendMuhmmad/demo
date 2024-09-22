package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.mapper.ProductMapper;
import com.example.demo.model.dto.ProductDto;
import com.example.demo.model.dto.ProductUpdateDto;
import com.example.demo.model.dto.ProductUpdateStockQuantityDTO;
import com.example.demo.service.ProductService;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    public ProductService productService;

    @PostMapping("/createProduct")
    public ResponseEntity<String> createProduct(@RequestBody ProductDto productdto) {
	return productService.save(ProductMapper.INSTANCE.mapCreateProduct(productdto), productdto.getLoginId());
    }

    @PutMapping("/updateProductStockQuantity")
    public ResponseEntity<String> updateProductStockQuantity(@RequestBody ProductUpdateStockQuantityDTO productdto) {
	return productService.updateProductQuantity(productdto.getId(), productdto.getStockQuantity(), productdto.getLoginId());
    }

    @PutMapping("/updateProduct")
    public ResponseEntity<String> updateProduct(@RequestBody ProductUpdateDto productUpdateDto) {
	return productService.save(ProductMapper.INSTANCE.mapUpdateProduct(productUpdateDto), productUpdateDto.getLoginId());
    }

    @DeleteMapping("/deleteProduct/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable int productId, @RequestParam int loginId) {
	return productService.deleteProduct(productId, loginId);
    }

    @GetMapping("/getAllProduct")
    public List<ProductDto> getAllProduct() {
	return ProductMapper.INSTANCE.mapProducts(productService.getAllProduct());
    }

}
