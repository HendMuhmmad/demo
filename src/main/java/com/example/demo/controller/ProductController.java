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
import com.example.demo.model.dto.CreateProductDTO;
import com.example.demo.model.dto.ProductUpdateStockQuantityDTO;
import com.example.demo.service.ProductService;

@RestController
@RequestMapping("/api/v1")
public class ProductController {
	
	@Autowired
	public ProductService productService;


	public ProductController(ProductService ps) {
		this.productService=ps;
		
	}
	
	
	@PostMapping("/createProduct")
	public ResponseEntity<String> postProduct(@RequestBody CreateProductDTO productdto){

		int ProductId=(int) productService.save( ProductMapper.INSTANCE.mapCreateProduct(productdto)
									,productdto.getLoginId());
		if(ProductId != -1) {
			return  ResponseEntity.ok("ID="+ProductId+"\n" +"Product added successfully");
		}else {
			return ResponseEntity.badRequest().body("Product addition failed-Unautherized");
		}
	}
	
	@PutMapping("/updateProduct/quantity")
	public ResponseEntity<String> updateProductStockQuantity(@RequestBody ProductUpdateStockQuantityDTO productdto){
	     boolean updated = productService.updateProductQuantity(productdto.getId(), productdto.getStockQuantity(), productdto.getLoginId());

	        if (updated) {
	            return ResponseEntity.ok("Product quantity updated successfully.");
	        } else {
	            return ResponseEntity.badRequest().body("Failed to update product quantity.");
	        }
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
