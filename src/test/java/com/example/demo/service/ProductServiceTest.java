package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.example.demo.enums.RoleEnum;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.User;
import com.example.demo.repository.ProductRepository;

@SpringBootTest
public class ProductServiceTest {
    @Autowired
    public ProductService productService;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    public UserService userService;

    @Test
    public void saveProduct_InvalidCustomerId() {
        User customer = createUserWithRoleId(RoleEnum.CUSTOMER.getCode());
        Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(dummyProduct());
        Mockito.when(userService.getUserById(Mockito.any(Long.class))).thenReturn(Optional.of(customer));
        assertThrows(BusinessException.class, () -> {
            productService.save(dummyProduct(), customer.getId());
        });
    }

    @Test
    public void saveProduct_InvalidHeadOfDepartmentId() {
        User headOfDepartment = createUserWithRoleId(RoleEnum.HEAD_OF_DEPARTMENT.getCode());
        Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(dummyProduct());
        Mockito.when(userService.getUserById(Mockito.any(Long.class))).thenReturn(Optional.of(headOfDepartment));
        assertThrows(BusinessException.class, () -> {
            productService.save(dummyProduct(), headOfDepartment.getId());
        });
    }

    @Test
    public void saveProduct_withProductPriceIsNull() {
        Product product = Product.builder().id(1L).productName("Laptop").color("Silver").stockQuantity(50)
                .description("High-performance laptop").build();
        User adminUser = createUserWithRoleId(RoleEnum.ADMIN.getCode());
        Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(product);
        Mockito.when(userService.getUserById(Mockito.any(Long.class))).thenReturn(Optional.of(adminUser));
        assertThrows(BusinessException.class, () -> {
            productService.save(product, adminUser.getId());
        });
    }

    @Test
    public void saveProduct_withProductNameIsNull() {
        Product product = Product.builder().id(1L).price(1200.00).color("Silver").stockQuantity(50)
                .description("High-performance laptop").build();
        User adminUser = createUserWithRoleId(RoleEnum.ADMIN.getCode());
        Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(product);
        Mockito.when(userService.getUserById(Mockito.any(Long.class))).thenReturn(Optional.of(adminUser));
        assertThrows(BusinessException.class, () -> {
            productService.save(product, adminUser.getId());
        });
    }

    @Test
    public void saveProduct_withLoginIdIsZero() {
        User roleZeroUser = createUserWithRoleId(0L);
        Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(dummyProduct());
        Mockito.when(userService.getUserById(Mockito.any(Long.class))).thenReturn(Optional.of(roleZeroUser));
        assertThrows(BusinessException.class, () -> {
            productService.save(dummyProduct(), roleZeroUser.getId());
        });
    }

    @Test
    public void deleteProduct_validRoleAndProductId() {
        User adminUser = createUserWithRoleId(RoleEnum.ADMIN.getCode());
        Mockito.when(userService.getUserById(Mockito.any(Long.class))).thenReturn(Optional.of(adminUser));
        Mockito.when(productRepository.findById(Mockito.eq(1L))).thenReturn(dummyOptionalProduct());
        productService.deleteProduct(1L, adminUser.getId());
        Mockito.verify(productRepository).deleteById(1L);
    }

    @Test
    public void deleteProduct_invalidProductId() {
        User adminUser = createUserWithRoleId(RoleEnum.ADMIN.getCode());
        Mockito.when(userService.getUserById(Mockito.any(Long.class))).thenReturn(Optional.of(adminUser));
        Mockito.when(productRepository.findById(Mockito.eq(1L))).thenReturn(dummyOptionalProduct());
        assertThrows(BusinessException.class, () -> {
            productService.deleteProduct(8L, adminUser.getId());
        });
    }

    @Test
    public void deleteProduct_noProducts() {
        User adminUser = createUserWithRoleId(RoleEnum.ADMIN.getCode());
        Mockito.when(userService.getUserById(Mockito.any(Long.class))).thenReturn(Optional.of(adminUser));
        Mockito.when(productRepository.findById(Mockito.eq(1L))).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> {
            productService.deleteProduct(1L, adminUser.getId());
        });
    }

    @Test
    public void deleteProduct_ValidSuperAdminId() {
        User superAdminUser = createUserWithRoleId(RoleEnum.SUPER_ADMIN.getCode());
        Mockito.when(userService.getUserById(Mockito.any(Long.class))).thenReturn(Optional.of(superAdminUser));
        Mockito.when(productRepository.findById(Mockito.eq(1L))).thenReturn(dummyOptionalProduct());
        productService.deleteProduct(1L, superAdminUser.getId());
        Mockito.verify(productRepository).deleteById(1L);
    }

    @Test
    public void deleteProduct_InvalidCustomerId() {
        User customer = createUserWithRoleId(RoleEnum.CUSTOMER.getCode());
        Mockito.when(userService.getUserById(Mockito.any(Long.class))).thenReturn(Optional.of(customer));
        Mockito.when(productRepository.findById(Mockito.eq(1L))).thenReturn(dummyOptionalProduct());
        assertThrows(BusinessException.class, () -> {
            productService.deleteProduct(1L, customer.getId());
        });
    }

    @Test
    public void deleteProduct_InvalidHeadOfDepartmentId() {
        User headOfDepartment = createUserWithRoleId(RoleEnum.HEAD_OF_DEPARTMENT.getCode());
        Mockito.when(userService.getUserById(Mockito.any(Long.class))).thenReturn(Optional.of(headOfDepartment));
        Mockito.when(productRepository.findById(Mockito.eq(1L))).thenReturn(dummyOptionalProduct());
        assertThrows(BusinessException.class, () -> {
            productService.deleteProduct(1L, headOfDepartment.getId());
        });
    }

    @Test
    public void updateProductQuantity_invalidProductId() {
        User adminUser = createUserWithRoleId(RoleEnum.ADMIN.getCode());
        Mockito.when(userService.getUserById(Mockito.any(Long.class))).thenReturn(Optional.of(adminUser));
        Mockito.when(productRepository.findById(Mockito.eq(1L))).thenReturn(dummyOptionalProduct());
        assertThrows(BusinessException.class, () -> {
            productService.updateProductQuantity(8L, 100, adminUser.getId());
        });
    }

    @Test
    public void updateProductQuantity_ValidAdminId() {
        User adminUser = createUserWithRoleId(RoleEnum.ADMIN.getCode());
        Mockito.when(userService.getUserById(Mockito.any(Long.class))).thenReturn(Optional.of(adminUser));
        Mockito.when(productRepository.findById(Mockito.eq(1L))).thenReturn(dummyOptionalProduct());
        productService.updateProductQuantity(1L, 100, adminUser.getId());
        Mockito.verify(productRepository).save(Mockito.argThat(p -> p.getStockQuantity() == 100));
    }

    @Test
    public void updateProductQuantity_ValidSuperAdminId() {
        User superAdmin = createUserWithRoleId(RoleEnum.SUPER_ADMIN.getCode());
        Mockito.when(userService.getUserById(Mockito.any(Long.class))).thenReturn(Optional.of(superAdmin));
        Mockito.when(productRepository.findById(Mockito.eq(1L))).thenReturn(dummyOptionalProduct());
        productService.updateProductQuantity(1L, 100, superAdmin.getId());
        Mockito.verify(productRepository).save(Mockito.argThat(p -> p.getStockQuantity() == 100));
    }

    @Test
    public void updateProductQuantity_InvalidCustomerId() {
        User customer = createUserWithRoleId(RoleEnum.CUSTOMER.getCode());
        Mockito.when(userService.getUserById(Mockito.any(Long.class))).thenReturn(Optional.of(customer));
        Mockito.when(productRepository.findById(Mockito.eq(1L))).thenReturn(dummyOptionalProduct());
        assertThrows(BusinessException.class, () -> {
            productService.updateProductQuantity(1L, 50, customer.getId());
        });
    }

    @Test
    public void updateProductQuantity_InvalidHeadOfDepartmentId() {
        User headOfDepartment = createUserWithRoleId(RoleEnum.HEAD_OF_DEPARTMENT.getCode());
        Mockito.when(userService.getUserById(Mockito.any(Long.class))).thenReturn(Optional.of(headOfDepartment));
        Mockito.when(productRepository.findById(Mockito.eq(1L))).thenReturn(dummyOptionalProduct());
        assertThrows(BusinessException.class, () -> {
            productService.updateProductQuantity(1L, 50, headOfDepartment.getId());
        });
    }

    private User createUserWithRoleId(Long roleId) {
    	return new User(1L, "", "", roleId, "", "", "", "", "", "", new Date(), new Date(), null);
    }

    private Product dummyProduct() {
        return Product.builder().id(1L).productName("Laptop").price(1200.00).color("Silver").stockQuantity(50)
                .description("High-performance laptop").build();
    }

    private Optional<Product> dummyOptionalProduct() {
        return Optional.of(dummyProduct());
    }
}