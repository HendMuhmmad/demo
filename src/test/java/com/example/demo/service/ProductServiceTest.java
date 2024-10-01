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

import com.example.demo.enums.RoleEnum;
import com.example.demo.exception.BusinessException;
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
	Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(dummyProduct());
	Long savedProductId = productService.save(dummyProduct(), RoleEnum.ADMIN.getCode());
	assertEquals(1, (long)savedProductId);
    }

    @Test
    public void saveProduct_ValidSuperAdminId() {
	Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(dummyProduct());
	Long savedProductId = productService.save(dummyProduct(), RoleEnum.SUPER_ADMIN.getCode());
	assertEquals(1, (long)savedProductId);
    }

    @Test
    public void saveProduct_InvalidCustomerId() {
	Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(dummyProduct());
	assertThrows(BusinessException.class, () -> {
	    productService.save(dummyProduct(), RoleEnum.CUSTOMER.getCode());
	});
    }

    @Test
    public void saveProduct_InvalidHeadOfDepartmentId() {
	Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(dummyProduct());
	assertThrows(BusinessException.class, () -> {
	    productService.save(dummyProduct(), RoleEnum.HEAD_OF_DEPARTMENT.getCode());
	});
    }

    @Test
    public void saveProduct_withProductPriceIsNull() {
	Product product = Product.builder()
		.id(1L)
		.productName("Laptop")
		.color("Silver")
		.stockQuantity(50)
		.description("High-performance laptop")
		.build();
	Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(product);
	assertThrows(BusinessException.class, () -> {
	    productService.save(product, RoleEnum.ADMIN.getCode());
	});
    }

    @Test
    public void saveProduct_withProductNameIsNull() {
	Product product = Product.builder()
		.id(1L)
		.price(1200.00)
		.color("Silver")
		.stockQuantity(50)
		.description("High-performance laptop")
		.build();
	Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(product);
	assertThrows(BusinessException.class, () -> {
	    productService.save(product, RoleEnum.ADMIN.getCode());
	});
    }

    @Test
    public void saveProduct_withloginIdIsZero() {
	Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(dummyProduct());
	assertThrows(BusinessException.class, () -> {
	    productService.save(dummyProduct(), 0L);
	});
    }

    @Test
    public void deleteProduct_validRoleAndProductId() {
	Mockito.when(productRepository.findById(Mockito.eq(1L))).thenReturn(dummyOptionalProduct());
	productService.deleteProduct(1L, RoleEnum.ADMIN.getCode());
	Mockito.verify(productRepository).deleteById(1L); // Verifying that deleteById was called with the correct ID

    }

    // revise only if Mockito.eq(33) fails
    @Test
    public void deleteProduct_invalidProductId() {
	Mockito.when(productRepository.findById(Mockito.eq(1L))).thenReturn(dummyOptionalProduct());
	assertThrows(BusinessException.class, () -> {
	    productService.deleteProduct(8L, RoleEnum.ADMIN.getCode());
	});

    }

    @Test
    public void deleteProduct_noProducts() {
	Mockito.when(productRepository.findById(Mockito.eq(1L))).thenReturn(Optional.empty());
	assertThrows(BusinessException.class, () -> {
	    productService.deleteProduct(1L, RoleEnum.ADMIN.getCode());
	});

    }

    @Test
    public void deleteProduct_ValidSuperAdminId() {
	Mockito.when(productRepository.findById(Mockito.eq(1L))).thenReturn(dummyOptionalProduct());
	productService.deleteProduct(1L, RoleEnum.SUPER_ADMIN.getCode());
	Mockito.verify(productRepository).deleteById(1L); // Verifying that deleteById was called with the correct ID

    }

    @Test
    public void deleteProduct_InvalidCustomerId() {
	Mockito.when(productRepository.findById(Mockito.eq(1L))).thenReturn(dummyOptionalProduct());
	assertThrows(BusinessException.class, () -> {
	    productService.deleteProduct(1L, RoleEnum.CUSTOMER.getCode());
	});
    }

    @Test
    public void deleteProduct_InvalidHeadOfDepartmentId() {
	Mockito.when(productRepository.findById(Mockito.eq(1L))).thenReturn(dummyOptionalProduct());
	assertThrows(BusinessException.class, () -> {
	    productService.deleteProduct(1L, RoleEnum.HEAD_OF_DEPARTMENT.getCode());
	});
    }

    @Test
    public void updateProductQuantity_invalidProductId() {
	Mockito.when(productRepository.findById(Mockito.eq(1L))).thenReturn(dummyOptionalProduct());
	assertThrows(BusinessException.class, () -> {
	    productService.updateProductQuantity(8L, 100, RoleEnum.ADMIN.getCode());
	});
    }

    @Test
    public void updateProductQuantity_ValidAdminId() {
	Mockito.when(productRepository.findById(Mockito.eq(1L))).thenReturn(dummyOptionalProduct());
	productService.updateProductQuantity(1L, 100, RoleEnum.ADMIN.getCode());
	Mockito.verify(productRepository).save(Mockito.argThat(p -> p.getStockQuantity() == 100)); // Verify that the stock quantity was updated to 100

    }

    @Test
    public void updateProductQuantity_ValidSuperAdminId() {
	Mockito.when(productRepository.findById(Mockito.eq(1L))).thenReturn(dummyOptionalProduct());
	productService.updateProductQuantity(1L, 100, RoleEnum.SUPER_ADMIN.getCode());
	Mockito.verify(productRepository).save(Mockito.argThat(p -> p.getStockQuantity() == 100)); // Verify that the stock quantity was updated to 100
    }

    @Test
    public void updateProductQuantity_InvalidCustomerId() {
	Mockito.when(productRepository.findById(Mockito.eq(1L))).thenReturn(dummyOptionalProduct());
	assertThrows(BusinessException.class, () -> {
	    productService.updateProductQuantity(1L, 50, RoleEnum.CUSTOMER.getCode());
	});
    }

    @Test
    public void updateProductQuantity_InvalidHeadOfDepartmentId() {
	Mockito.when(productRepository.findById(Mockito.eq(1L))).thenReturn(dummyOptionalProduct());
	assertThrows(BusinessException.class, () -> {
	    productService.updateProductQuantity(1L, 50, RoleEnum.HEAD_OF_DEPARTMENT.getCode());
	});
    }

    @Test
    public void getAllProducts() {
	Product product1 = new Product(1L, "Product 1", 100.0, "Red", 10, "Description 1", null);
	Product product2 = new Product(2L, "Product 2", 200.0, "Blue", 20, "Description 2", null);

	List<Product> mockProducts = Arrays.asList(product1, product2);

	Mockito.when(productRepository.findAll()).thenReturn(mockProducts);

	List<Product> products = productService.getAllProduct();

	assertEquals(2, products.size());
	assertEquals("Product 1", products.get(0).getProductName());
	assertEquals("Product 2", products.get(1).getProductName());
    }

    public Optional<Product> dummyOptionalProduct() {
	return Optional.of(new Product(1L, "laptop", 1200, "silver", 50, "laptop description", null));
    }

    public Product dummyProduct() {
	return Product.builder()
		.id(1L)
		.productName("Laptop")
		.price(1200.00)
		.color("Silver")
		.stockQuantity(50)
		.description("High-performance laptop")
		.build();
    }

}
