package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import com.example.demo.enums.workflow.WFProcessesEnum;
import com.example.demo.enums.workflow.WFProductEnum;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.model.dto.ProductDto;
import com.example.demo.model.dto.ProductUpdateDto;
import com.example.demo.model.dto.ProductUpdateStockQuantityDTO;
import com.example.demo.model.orm.Product;
import com.example.demo.service.ProductService;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    public ProductService productService;
   

    @PostMapping("/createProduct")
    public ResponseEntity<String> createProduct(@RequestBody ProductDto productDto) {
        Long loginId = productDto.getLoginId();
        Product product = ProductMapper.INSTANCE.mapCreateProduct(productDto);
        String msg = productService.request(product, loginId,WFProcessesEnum.ADD_PRODUCT.getCode());
        return ResponseEntity.status(HttpStatus.OK).body(msg);
    }


    @PutMapping("/updateProductStockQuantity")
    public ResponseEntity<String> updateProductStockQuantity(@RequestBody ProductUpdateStockQuantityDTO productdto) {
	productService.updateProductQuantity(productdto.getId(), productdto.getStockQuantity(), productdto.getLoginId());
	return ResponseEntity.ok("Product stock quantity updated successfully");

    }

    @PutMapping("/updateProduct")
    public ResponseEntity<String> updateProduct(@RequestBody ProductUpdateDto productUpdateDto) {
    String msg = productService.request(ProductMapper.INSTANCE.mapUpdateProduct(productUpdateDto), productUpdateDto.getLoginId(),WFProcessesEnum.UPDATE_PRODUCT.getCode());
    return ResponseEntity.status(HttpStatus.OK).body(msg);

    }

    @DeleteMapping("/deleteProduct/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId, @RequestParam Long loginId) {
    String msg = productService.requestDeleteProduct(productId, loginId);
    return ResponseEntity.status(HttpStatus.OK).body(msg);


    }

    @GetMapping("/getAllProduct")
    public List<ProductDto> getAllProduct() {
	return ProductMapper.INSTANCE.mapProducts(productService.getAllProduct());
    }
    
}
