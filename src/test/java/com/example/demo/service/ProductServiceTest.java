package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.example.demo.enums.RoleEnum;
import com.example.demo.enums.workflow.WFInstanceStatusEnum;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.User;
import com.example.demo.model.orm.workflow.WFInstance;
import com.example.demo.model.orm.workflow.WFProduct;
import com.example.demo.model.orm.workflow.WFTask;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.workflow.WFInstanceRepository;
import com.example.demo.repository.workflow.WFProductRepository;
import com.example.demo.repository.workflow.WFTaskRepository;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
	
    @Autowired
    public ProductService productService;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    public UserService userService;

    @MockBean
    public WFProductServiceImpl wfProductService;
    
    
    @MockBean
    private UserRepository userRepository;

    @MockBean 
    WFInstanceRepository wfInstanceRepository;
    
    @MockBean 
    WFTaskRepository wfTaskRepository;
    
    @MockBean 
    WFProductRepository wfProductRepository;
    

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
        Product product = Product.builder().productName("Laptop").color("Silver").stockQuantity(50)
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
        Product product = Product.builder().price(1200.00).color("Silver").stockQuantity(50)
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
        User superAdminUser = createUserWithRoleId(RoleEnum.SUPER_ADMIN.getCode());
        Mockito.when(userService.getUserById(Mockito.any(Long.class))).thenReturn(Optional.of(superAdminUser));
        Mockito.when(productRepository.findById(Mockito.eq(1L))).thenReturn(dummyOptionalProduct());
        productService.deleteProduct(1L, superAdminUser.getId());
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
        Mockito.when(productRepository.findById(Mockito.any())).thenThrow(new BusinessException(""));
        assertThrows(BusinessException.class, () -> {
            productService.updateProductQuantity(8L, 100, adminUser.getId());
        });
    }

    @Test
    public void updateProductQuantity_ValidAdminId() {
        User adminUser = createUserWithRoleId(RoleEnum.ADMIN.getCode());
        User superAdminUser = createUserWithRoleId(RoleEnum.SUPER_ADMIN.getCode());
        List<User> superAdmins = new ArrayList<>(Collections.singletonList(superAdminUser));
        Mockito.when(userService.getUserById(Mockito.any(Long.class))).thenReturn(Optional.of(adminUser));
        Mockito.when(productRepository.findById(Mockito.eq(1L))).thenReturn(dummyOptionalProduct());
        Mockito.when(userRepository.findByRoleId(RoleEnum.SUPER_ADMIN.getCode())).thenReturn(superAdmins);
        // Arrange
        when(wfTaskRepository.save(Mockito.any())).thenReturn(new WFTask());
        when(wfInstanceRepository.save(Mockito.any())).thenReturn(new WFInstance(1L,1L,1L,new Date(),WFInstanceStatusEnum.RUNNING.getCode()));
        when(wfProductRepository.save(Mockito.any())).thenReturn(new WFProduct());
        productService.updateProductQuantity(1L, 100, adminUser.getId());       
        Mockito.verify(productRepository,times(0)).save(any());
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
        return Product.builder().productName("Laptop").price(1200.00).color("Silver").stockQuantity(50)
                .description("High-performance laptop").build();
    }

    private Optional<Product> dummyOptionalProduct() {
        return Optional.of(dummyProduct());
    }
}