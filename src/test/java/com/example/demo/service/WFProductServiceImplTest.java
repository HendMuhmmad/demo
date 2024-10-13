package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.enums.RoleEnum;
import com.example.demo.enums.WFActionEnum;
import com.example.demo.enums.WFAsigneeRoleEnum;
import com.example.demo.enums.WFProductActionStatusEnum;
import com.example.demo.enums.workflow.WFInstanceStatusEnum;
import com.example.demo.enums.workflow.WFProcessesEnum;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.ProductTrnsHistory;
import com.example.demo.model.orm.User;
import com.example.demo.model.orm.WFProduct;
import com.example.demo.model.orm.workflow.WFInstance;
import com.example.demo.model.orm.workflow.WFTask;
import com.example.demo.model.orm.workflow.WFTaskDetails;
import com.example.demo.repository.ProductTrnsHistoryRepository;
import com.example.demo.repository.workflow.WFInstanceRepository;
import com.example.demo.repository.workflow.WFProductRepository;
import com.example.demo.repository.workflow.WFTaskDetailsRepository;
import com.example.demo.repository.workflow.WFTaskRepository;

@ExtendWith(MockitoExtension.class)
class WFProductServiceImplTest {
	
	 

    @Mock
    private WFInstanceRepository wfInstanceRepository;
    
    @Mock
    private WFProductRepository wfProductRepository;

    @Mock
    private UserService userService;

    @Mock
    private WFTaskRepository wfTaskRepository;

    @Mock
    private WFTaskDetailsRepository wfTaskDetailsRepository;
    
    @Mock 
    private ProductService productService;

    @Mock
    private ProductTrnsHistoryRepository productTrnsHistoryRepository;
    
    @Mock
	private ProductTrnsHistoryServiceImpl productTrnsHistoryService;
    
    @InjectMocks
    private WFProductServiceImpl wfProductService;

    private WFInstance wfInstance;
    private WFTask wfTask;
    private WFProduct wfProduct;
    private Product product;
    private ProductTrnsHistory productTrnsHistory;
    
    @BeforeEach
    void setUp() {
 
        wfInstance = new WFInstance();
        wfInstance.setId(1L);
        wfInstance.setStatus(WFInstanceStatusEnum.RUNNING.getCode());
        wfInstance.setRequesterId(1L);

        wfProduct = new WFProduct();
        wfProduct.setProductId(1L);
        wfProduct.setWfInstanceId(wfInstance.getId());
        
        product = new Product();
        product.setId(1L);
        
        productTrnsHistory = new ProductTrnsHistory();
        
        wfTask = new WFTask();
        wfTask.setId(1L);
        wfTask.setInstanceId(wfInstance.getId());
        wfTask.setAssigneeId(1L);
        wfTask.setAssigneeRole(WFAsigneeRoleEnum.SUPER_ADMIN.getCode());
    }

 

    @Test
    void addWFProduct_success() {
    	List<User>userList = new LinkedList();
    	 
    	User superAdmin = createUserWithRoleId(RoleEnum.SUPER_ADMIN.getCode());
    	userList.add(superAdmin);
    	when(userService.getUsesrByRole(anyLong())).thenReturn(userList);
    	when(userService.getUserById(superAdmin.getId())).thenReturn(Optional.of(superAdmin));
        long requesterId = superAdmin.getId();
        Product product = new Product();
        product.setProductName("Test Product");
        product.setPrice(100.0);
        product.setColor("Red");
        product.setStockQuantity(50);
        product.setDescription("This is a test product.");

        // Call the method under test
        
        assertDoesNotThrow(() -> wfProductService.addWFProduct(requesterId, product));
        verify(wfTaskRepository).save(any(WFTask.class));
        
        
 
    }
    
    @Test
    void updateWFProduct_success() {
        List<User> userList = new LinkedList<>();
        
        User superAdmin = createUserWithRoleId(RoleEnum.SUPER_ADMIN.getCode());
        userList.add(superAdmin);
        when(userService.getUsesrByRole(anyLong())).thenReturn(userList);
        
       	when(userService.getUserById(superAdmin.getId())).thenReturn(Optional.of(superAdmin));
        long requesterId = superAdmin.getId();
        long oldProductId = 123L; // Example old product ID
        Product product = new Product();
        product.setProductName("Updated Test Product");
        product.setPrice(120.0);
        product.setColor("Blue");
        product.setStockQuantity(30);
        product.setDescription("This is an updated test product.");

        // Call the method under test
        assertDoesNotThrow(() -> wfProductService.updateWFProduct(requesterId, oldProductId, product));
        verify(wfTaskRepository).save(any(WFTask.class));
    }
    
    
    @Test
    void deleteWFProduct_success() {
        List<User> userList = new LinkedList<>();
        
        User superAdmin = createUserWithRoleId(RoleEnum.SUPER_ADMIN.getCode());
        userList.add(superAdmin);
        when(userService.getUsesrByRole(anyLong())).thenReturn(userList);
        
       	when(userService.getUserById(superAdmin.getId())).thenReturn(Optional.of(superAdmin));
        long requesterId = superAdmin.getId();
        long oldProductId = 123L; // Example old product ID

        // Call the method under test
        assertDoesNotThrow(() -> wfProductService.deleteWFProduct(requesterId, oldProductId));
        verify(wfTaskRepository).save(any(WFTask.class));
    }
    
    @Test
    void addWFProduct_unAuthorized() {
    	List<User>userList = new LinkedList();
    	 
 
    	User admin = createUserWithRoleId(RoleEnum.ADMIN.getCode());
    	when(userService.getUserById(admin.getId())).thenReturn(Optional.of(admin));
        long requesterId = admin.getId();
        Product product = new Product();
        product.setProductName("Test Product");
        product.setPrice(100.0);
        product.setColor("Red");
        product.setStockQuantity(50);
        product.setDescription("This is a test product.");

        // Call the method under test

        assertThrows(BusinessException.class, () -> wfProductService.addWFProduct(requesterId, product));
 
        
 
    }
    
    @Test
    void changeRequestStatus_taskNotFound() {
      
        when(wfTaskRepository.findById(1L)).thenReturn(Optional.empty());

       
        assertThrows(BusinessException.class, () -> wfProductService.changeRequestStatus(1L, 1L, true));
    }

    @Test
    void changeRequestStatus_userNotAuthorized() {
       
        wfTask.setAssigneeId(2L);  
        when(wfTaskRepository.findById(1L)).thenReturn(Optional.of(wfTask));
        
     
        assertThrows(BusinessException.class, () -> wfProductService.changeRequestStatus(1L, 1L, true));
    }

    @Test
    void changeRequestStatus_approvesTaskSuccessfully() {
    	
        when(wfTaskRepository.findById(1L)).thenReturn(Optional.of(wfTask));
        when(wfInstanceRepository.findById(wfInstance.getId())).thenReturn(Optional.of(wfInstance));
        when(wfProductRepository.findByWfInstanceId(wfInstance.getId())).thenReturn(Optional.of(wfProduct));
        when(productService.findbyId(product.getId())).thenReturn(product);
 
 
        wfProductService.changeRequestStatus(1L, 1L, true);
        
        
        assertEquals(WFActionEnum.APPROVED.getCode(), wfTask.getActionId());
  
        assertNotNull(wfTask.getActionDate());
        
        verify(wfInstanceRepository, times(1)).save(wfInstance);
    }

    @Test
    void getProductIdByTaskId_successReturn() {
    
        when(wfTaskRepository.findById(1L)).thenReturn(Optional.of(wfTask));
        when(wfProductRepository.findByWfInstanceId(wfInstance.getId())).thenReturn(Optional.of(wfProduct));

    
        long productId = wfProductService.getProductIdByTaskId(1L);
        assertEquals(1L, productId);
    }

    @Test
    void getUserTasks_successReturn() {
        
        List<WFTaskDetails> wfTaskDetailsList = List.of(new WFTaskDetails());
        when(wfTaskDetailsRepository.findByAssigneeId(1L)).thenReturn(wfTaskDetailsList);

 
        List<WFTaskDetails> result = wfProductService.getUserTasks(1L);
        assertEquals(wfTaskDetailsList, result);
    }
    
    
 

 
    private User createUserWithRoleId(Long roleId) {
    	return new User(1L, "", "", roleId, "", "", "", "", "", "", new Date(), new Date(), null);
    }
}
