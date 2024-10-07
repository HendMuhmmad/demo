package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.example.demo.enums.RoleEnum;
import com.example.demo.enums.workflow.WFStatusEnum;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.dto.TaskRequestDto;
import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.User;
import com.example.demo.model.orm.workflow.WFTask;
import com.example.demo.model.orm.workflow.WFTaskDetails;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.workflow.WFTaskDetailsRepository;

@SpringBootTest
public class ProductRequestServiceTest {

    @Autowired
    public ProductService productService;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    public UserService userService;

    @MockBean
    private WFProductService wfProductService;
    
    @MockBean
    private WFTaskDetailsRepository wfTaskDetailsRepository;

    private Product product;
    private WFTask wfTask;
    private WFTaskDetails taskDetails;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setProductName("Sample Product");
        product.setPrice(29.99);
        product.setWfStatus(0);

        wfTask = new WFTask();
        wfTask.setId(1L);
        wfTask.setAssigneeId(1L);
        
        taskDetails = new WFTaskDetails();
        taskDetails.setTaskId(1L);
        taskDetails.setAssigneeId(1L);
        taskDetails.setDescription("Sample Task");
    }


    @Test
    void testRequestAuthorizedForAdmin() {
        Long loginId = 1L;
        User user = new User();
        user.setId(loginId);
        user.setFirstName("User");
        user.setRoleId(RoleEnum.ADMIN.getCode());

        when(userService.getUserById(loginId)).thenReturn(Optional.of(user));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        String result = productService.request(product, loginId);

        assertEquals("Your request is sent successfully", result);
    }

    @Test
    void testRequestAuthorizedForSuperAdmin() {
        Long loginId = 2L;
        User user = new User();
        user.setId(loginId);
        user.setFirstName("SuperAdmin");
        user.setRoleId(RoleEnum.SUPER_ADMIN.getCode());

        when(userService.getUserById(loginId)).thenReturn(Optional.of(user));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        String result = productService.request(product, loginId);

        assertEquals("Your product is done successfully", result);
    }

    @Test
    void testRequestUnauthorized() {
        Long loginId = 3L;
        User user = new User();
        user.setId(loginId);
        user.setFirstName("Customer");
        user.setRoleId(RoleEnum.CUSTOMER.getCode());

        when(userService.getUserById(loginId)).thenReturn(Optional.of(user));

        BusinessException thrown = 
            org.junit.jupiter.api.Assertions.assertThrows(BusinessException.class, () -> {
                productService.request(product, loginId);
            });
        
        assertEquals("You are not authorizted to create a product", thrown.getMessage());
    }

    @Test
    void testRespondToRequest_Approved() {
        TaskRequestDto taskRequest = new TaskRequestDto();
        taskRequest.setTaskId(1L);
        taskRequest.setUserId(1L);
        taskRequest.setApproved(true);

        when(wfProductService.respondToRequest(taskRequest)).thenReturn(product.getId());
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        productService.respondToRequest(taskRequest);

        assertEquals(WFStatusEnum.APPROVED.getCode(), product.getWfStatus());
    }

    @Test
    void testRespondToRequest_Rejected() {
        TaskRequestDto taskRequest = new TaskRequestDto();
        taskRequest.setTaskId(1L);
        taskRequest.setUserId(1L);
        taskRequest.setApproved(false);

        when(wfProductService.respondToRequest(taskRequest)).thenReturn(product.getId());
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        productService.respondToRequest(taskRequest);

        assertEquals(WFStatusEnum.REJECTED.getCode(), product.getWfStatus());
    }

    @Test
    void testRespondToRequest_ProductNotFound() {
        TaskRequestDto taskRequest = new TaskRequestDto();
        taskRequest.setTaskId(1L);
        taskRequest.setUserId(1L);
        taskRequest.setApproved(true);

        when(wfProductService.respondToRequest(taskRequest)).thenReturn(product.getId());
        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

        BusinessException thrown = assertThrows(BusinessException.class, () -> {
            productService.respondToRequest(taskRequest);
        });

        assertEquals("There isn't a product with this id", thrown.getMessage());
    }

    @Test
    void testRespondToRequest_TaskAlreadyProcessed() {
        TaskRequestDto taskRequest = new TaskRequestDto();
        taskRequest.setTaskId(1L);
        taskRequest.setUserId(1L);
        taskRequest.setApproved(true);

        product.setWfStatus(WFStatusEnum.APPROVED.getCode());
        when(wfProductService.respondToRequest(taskRequest)).thenReturn(product.getId());
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        BusinessException thrown = assertThrows(BusinessException.class, () -> {
            productService.respondToRequest(taskRequest);
        });

        assertEquals("This task is already done", thrown.getMessage());
    }

    @Test
    void testRespondToRequest_UnauthorizedUser() {
        TaskRequestDto taskRequest = new TaskRequestDto();
        taskRequest.setTaskId(1L);
        taskRequest.setUserId(2L); 

        when(wfProductService.respondToRequest(taskRequest)).thenReturn(product.getId());

        wfTask.setAssigneeId(1L);
        when(wfProductService.respondToRequest(taskRequest)).thenThrow(new BusinessException("You dont have the right to respond to this task"));

        BusinessException thrown = assertThrows(BusinessException.class, () -> {
            productService.respondToRequest(taskRequest);
        });

        assertEquals("You dont have the right to respond to this task", thrown.getMessage());
    }
        
}
