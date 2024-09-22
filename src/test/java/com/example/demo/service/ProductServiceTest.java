package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.demo.enums.RoleEnum;
import com.example.demo.model.orm.Product;
import com.example.demo.repository.ProductRepository;

@SpringBootTest
public class ProductServiceTest {
	  @Autowired
	  public ProductService productService;
	  
	  @MockBean
	  private ProductRepository productRepository;
	  
	  @Test
	  public void saveProduct_ValidAdminId() {
		  Product product = Product.builder()
	        .id(1)
	        .productName("Laptop")
	        .price(1200.00)
	        .color("Silver")
	        .stockQuantity(50)
	        .description("High-performance laptop")
	        .build();
		  Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(product);
		  ResponseEntity<String> response = productService.save(product, RoleEnum.ADMIN.getCode());
		  assertEquals( HttpStatus.CREATED,response.getStatusCode());  
	  }
	  
	  @Test
	  public void saveProduct_ValidSuperAdminId() {
		  Product product = Product.builder()
	        .id(1)
	        .productName("Laptop")
	        .price(1200.00)
	        .color("Silver")
	        .stockQuantity(50)
	        .description("High-performance laptop")
	        .build();
		  Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(product);
		  ResponseEntity<String> response = productService.save(product, RoleEnum.SUPER_ADMIN.getCode());
		  assertEquals( HttpStatus.CREATED,response.getStatusCode());  
	  }
	  
	  @Test
	  public void saveProduct_InvalidCustomerId() {
		  Product product = Product.builder()
	        .id(1)
	        .productName("Laptop")
	        .price(1200.00)
	        .color("Silver")
	        .stockQuantity(50)
	        .description("High-performance laptop")
	        .build();
		  Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(product);
		  ResponseEntity<String> response = productService.save(product, RoleEnum.CUSTOMER.getCode());
		  assertEquals( HttpStatus.UNAUTHORIZED,response.getStatusCode());  
	  }
	  
	  @Test
	  public void saveProduct_InvalidHeadOfDepartmentId() {
		  Product product = Product.builder()
	        .id(1)
	        .productName("Laptop")
	        .price(1200.00)
	        .color("Silver")
	        .stockQuantity(50)
	        .description("High-performance laptop")
	        .build();
		  Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(product);
		  ResponseEntity<String> response = productService.save(product, RoleEnum.HEAD_OF_DEPARTMENT.getCode());
		  assertEquals( HttpStatus.UNAUTHORIZED,response.getStatusCode());  
	  }
	  
	  @Test
	  public void saveProduct_withProductPriceIsNull() {
		  Product product = Product.builder()
	        .id(1)
	        .productName("Laptop")
	        .color("Silver")
	        .stockQuantity(50)
	        .description("High-performance laptop")
	        .build();
		  Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(product);
		  ResponseEntity<String> response = productService.save(product, RoleEnum.ADMIN.getCode());
		  assertEquals( HttpStatus.BAD_REQUEST,response.getStatusCode());  
	  }
	  
	  @Test
	  public void saveProduct_withProductNameIsNull() {
		  Product product = Product.builder()
	        .id(1)
	        .price(1200.00)
	        .color("Silver")
	        .stockQuantity(50)
	        .description("High-performance laptop")
	        .build();
		  Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(product);
		  ResponseEntity<String> response = productService.save(product, RoleEnum.ADMIN.getCode());
		  assertEquals( HttpStatus.BAD_REQUEST,response.getStatusCode());  
	  }
	  
	  @Test
	  public void saveProduct_withloginIdIsZero() {
		  Product product = Product.builder()
	        .id(1)
	        .productName("Laptop")
	        .price(1200.00)
	        .color("Silver")
	        .stockQuantity(50)
	        .description("High-performance laptop")
	        .build();
		  Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(product);
		  ResponseEntity<String> response = productService.save(product, 0);
		  assertEquals( HttpStatus.UNAUTHORIZED,response.getStatusCode());  
	  }
	  
	  
	  @Test
	  public void deleteProduct_validRoleAndProductId() {
		  Optional<Product> product = Optional.of(new Product(1, "laptop", 1200, "silver", 50, "laptop description", null));
		  Mockito.when(productRepository.findById(Mockito.eq(1))).thenReturn(product);
		  ResponseEntity<String> response = productService.deleteProduct(1,  RoleEnum.ADMIN.getCode());
		  assertEquals( HttpStatus.OK,response.getStatusCode());  
	  }
	  
	  //revise only if Mockito.eq(33) fails
	  @Test
	  public void deleteProduct_invalidProductId() {
		  Optional<Product> product = Optional.of(new Product(1, "laptop", 1200, "silver", 50, "laptop description", null));
		  Mockito.when(productRepository.findById(Mockito.eq(1))).thenReturn(product);
		  ResponseEntity<String> response = productService.deleteProduct(8,  RoleEnum.ADMIN.getCode());
		  assertEquals( HttpStatus.NOT_FOUND,response.getStatusCode());  

//		  assertThrows(RuntimeException.class, () -> {
//	            productService.deleteProduct(33, RoleEnum.ADMIN.getCode());
//	        });
	  }
	  
	  
	  @Test
	  public void deleteProduct_noProducts() {
		  Mockito.when(productRepository.findById(Mockito.eq(1))).thenReturn(Optional.empty());
		  ResponseEntity<String> response = productService.deleteProduct(1,  RoleEnum.ADMIN.getCode());
		  assertEquals( HttpStatus.NOT_FOUND,response.getStatusCode());  

	  }	  
	
	  @Test
	  public void deleteProduct_ValidSuperAdminId() {
		  Optional<Product> product = Optional.of(new Product(1, "laptop", 1200, "silver", 50, "laptop description", null));
		  Mockito.when(productRepository.findById(Mockito.eq(1))).thenReturn(product);
		  ResponseEntity<String> response = productService.deleteProduct(1,  RoleEnum.SUPER_ADMIN.getCode());
		  assertEquals( HttpStatus.OK,response.getStatusCode());    
	  }
	  
	  @Test
	  public void deleteProduct_InvalidCustomerId() {
		  Optional<Product> product = Optional.of(new Product(1, "laptop", 1200, "silver", 50, "laptop description", null));
		  Mockito.when(productRepository.findById(Mockito.eq(1))).thenReturn(product);
		  ResponseEntity<String> response = productService.deleteProduct(1,  RoleEnum.CUSTOMER.getCode());
		  assertEquals( HttpStatus.UNAUTHORIZED,response.getStatusCode());  
	  }
	  
	  @Test
	  public void deleteProduct_InvalidHeadOfDepartmentId() {
		  Optional<Product> product = Optional.of(new Product(1, "laptop", 1200, "silver", 50, "laptop description", null));
		  Mockito.when(productRepository.findById(Mockito.eq(1))).thenReturn(product);
		  ResponseEntity<String> response = productService.deleteProduct(1,  RoleEnum.HEAD_OF_DEPARTMENT.getCode());
		  assertEquals( HttpStatus.UNAUTHORIZED,response.getStatusCode());  
	  }

	  
	  @Test
	  public void updateProductQuantity_invalidProductId() {
		  Optional<Product> product = Optional.of(new Product(1, "laptop", 1200, "silver", 50, "laptop description", null));
		  Mockito.when(productRepository.findById(Mockito.eq(1))).thenReturn(product);
		  ResponseEntity<String> response = productService.updateProductQuantity(8,50,  RoleEnum.ADMIN.getCode());
		  assertEquals( HttpStatus.NOT_FOUND,response.getStatusCode());  
	  }
	  
	  @Test
	  public void updateProductQuantity_ValidAdminId() {
		  Optional<Product> product = Optional.of(new Product(1, "laptop", 1200, "silver", 50, "laptop description", null));
		  Mockito.when(productRepository.findById(Mockito.eq(1))).thenReturn(product);
		  ResponseEntity<String> response = productService.updateProductQuantity(1,50,  RoleEnum.ADMIN.getCode());
		  assertEquals( HttpStatus.OK,response.getStatusCode());    
	  }
	  
	  @Test
	  public void updateProductQuantity_ValidSuperAdminId() {
		  Optional<Product> product = Optional.of(new Product(1, "laptop", 1200, "silver", 50, "laptop description", null));
		  Mockito.when(productRepository.findById(Mockito.eq(1))).thenReturn(product);
		  ResponseEntity<String> response = productService.updateProductQuantity(1,50,  RoleEnum.SUPER_ADMIN.getCode());
		  assertEquals( HttpStatus.OK,response.getStatusCode());    
	  }
	  
	  @Test
	  public void updateProductQuantity_InvalidCustomerId() {
		  Optional<Product> product = Optional.of(new Product(1, "laptop", 1200, "silver", 50, "laptop description", null));
		  Mockito.when(productRepository.findById(Mockito.eq(1))).thenReturn(product);
		  ResponseEntity<String> response = productService.updateProductQuantity(1,50,  RoleEnum.CUSTOMER.getCode());
		  assertEquals( HttpStatus.UNAUTHORIZED,response.getStatusCode());  
	  }
	  
	  @Test
	  public void updateProductQuantity_InvalidHeadOfDepartmentId() {
		  Optional<Product> product = Optional.of(new Product(1, "laptop", 1200, "silver", 50, "laptop description", null));
		  Mockito.when(productRepository.findById(Mockito.eq(1))).thenReturn(product);
		  ResponseEntity<String> response = productService.updateProductQuantity(1,50,   RoleEnum.HEAD_OF_DEPARTMENT.getCode());
		  assertEquals( HttpStatus.UNAUTHORIZED,response.getStatusCode());  
	  }
	  
	  @Test
	  public void getAllProducts() {
		  Product product1 = new Product(1, "Product 1", 100.0, "Red", 10, "Description 1", null);
	      Product product2 = new Product(2, "Product 2", 200.0, "Blue", 20, "Description 2", null);
	
	      List<Product> mockProducts = Arrays.asList(product1, product2);
	
	      Mockito.when(productRepository.findAll()).thenReturn(mockProducts);
	
	      List<Product> products = productService.getAllProduct();
	
	      assertEquals(2, products.size());
	      assertEquals("Product 1", products.get(0).getProductName());
	      assertEquals("Product 2", products.get(1).getProductName());
	  }

}
