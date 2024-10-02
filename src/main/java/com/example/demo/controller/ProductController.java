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
	Long productId = productService.save(ProductMapper.INSTANCE.mapCreateProduct(productdto), productdto.getLoginId());
	return ResponseEntity.ok("Product created successfully with ID: " + productId);
    }

    @PutMapping("/updateProductStockQuantity")
    public ResponseEntity<String> updateProductStockQuantity(@RequestBody ProductUpdateStockQuantityDTO productdto) {
	productService.updateProductQuantity(productdto.getId(), productdto.getStockQuantity(), productdto.getLoginId());
	return ResponseEntity.ok("Product stock quantity updated successfully");

    }

    @PutMapping("/updateProduct")
    public ResponseEntity<String> updateProduct(@RequestBody ProductUpdateDto productUpdateDto) {
	productService.save(ProductMapper.INSTANCE.mapUpdateProduct(productUpdateDto), productUpdateDto.getLoginId());
	return ResponseEntity.ok("Product updated successfully");

    }

    @DeleteMapping("/deleteProduct/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId, @RequestParam Long loginId) {
	productService.deleteProduct(productId, loginId);
	return ResponseEntity.ok("Product deleted successfully");

    }

    @GetMapping("/getAllProduct")
    public List<ProductDto> getAllProduct() {
	return ProductMapper.INSTANCE.mapProducts(productService.getAllProduct());
    }

    // @GetMapping("/tasks")
    // public ResponseEntity<List<ProductWorkflowTask>> getProductWorkflowTasks(@RequestParam Integer userId) {
    // List<ProductWorkflowTask> tasks = productWorkflowService.getTasksByUserId(userId);
    // return ResponseEntity.ok(tasks);
    // }

    @PostMapping("/tasks/approve")
    public ResponseEntity<String> approve(@RequestParam Integer taskId) {
	// productWorkflowService.approveTask(taskId);
	return ResponseEntity.ok("{\"message\":\"Approved Successfully\"}");
    }

    @PostMapping("/tasks/reject")
    public ResponseEntity<String> rejectProductRequest(@RequestParam Integer taskId) {
	// productWorkflowService.rejectTask(taskId);
	return ResponseEntity.ok("{\"message\":\"Rejected Successfully\"}");
    }
}
