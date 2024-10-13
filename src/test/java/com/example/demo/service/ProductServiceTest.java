package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.example.demo.enums.RoleEnum;
import com.example.demo.enums.workflow.WFProcessesEnum;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.User;
import com.example.demo.repository.ProductRepository;

@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductServiceImpl productService;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private WFProductService wfProductService;

    private Product product;
    private User admin;
    private User superAdmin;
    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setPrice(100);
        product.setProductName("Test Product");
        
        admin = new User();
        admin.setRoleId(RoleEnum.ADMIN.getCode());
        
        superAdmin = new User();
        superAdmin.setRoleId(RoleEnum.SUPER_ADMIN.getCode());


    }

    @Test
    void testRequestCreateProduct_SuccessfulForSuperAdmin() {
        when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(superAdmin));

        String result = productService.request(product, 1L, WFProcessesEnum.ADD_PRODUCT.getCode());

        verify(productRepository, times(1)).save(product);
        assertEquals("Your product is created successfully", result);
    }
    
    @Test
    void testRequestCreateProduct_SuccessfulForAdmin() {
        when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(admin));

        String result = productService.request(product, 1L, WFProcessesEnum.ADD_PRODUCT.getCode());

        assertEquals("Your request is sent successfully", result);
    }

    @Test
    void testRequestCreateProduct_ThrowsUnauthorizedForCustomer() {
        User customer = new User();
        customer.setRoleId(RoleEnum.CUSTOMER.getCode());

        when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(customer));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            productService.request(product, 1L, WFProcessesEnum.ADD_PRODUCT.getCode());
        });

        assertEquals("You are not authorized to create a product", exception.getMessage());
    }
    
    @Test
    void testRequestCreateProduct_ThrowsBusinessExceptionForMissingFields() {
        Product product = new Product(); 

        when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(admin));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            productService.request(product, 1L, WFProcessesEnum.ADD_PRODUCT.getCode());
        });

        assertEquals("Product price and name should not be null", exception.getMessage());
    }

    @Test
    void testRequestUpdateProduct_SuccessForAdmin() {
        when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(admin));
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.of(product));  // Ensure the product is found

        String result = productService.request(product, 1L, WFProcessesEnum.UPDATE_PRODUCT.getCode());

        verify(wfProductService, times(1)).initWFProduct(any(Product.class), anyLong(), eq(WFProcessesEnum.UPDATE_PRODUCT.getCode()));
        assertEquals("Your request is sent successfully", result);
    }

    @Test
    void testRequestUpdateProduct_ThrowsProductNotFound() {
        when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(admin));
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            productService.request(product, 1L, WFProcessesEnum.UPDATE_PRODUCT.getCode());
        });

        assertEquals("There is no product with this Id", exception.getMessage());
    }
    @Test
    void testRequestDeleteProduct_ThrowsProductNotFound() {
        when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(admin));
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            productService.requestDeleteProduct(1L, 1L);
        });

        assertEquals("There is no product with this Id", exception.getMessage());
    }

    @Test
    void testRequestDeleteProduct_SuccessForSuperAdmin() {
        when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(superAdmin));
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.of(product));

        String result = productService.requestDeleteProduct(1L, 1L);

        verify(wfProductService, times(1)).initWFProduct(any(Product.class), anyLong(), eq(WFProcessesEnum.DELETE_PRODUCT.getCode()));
        assertEquals("Your request is sent successfully", result);
    }


}
