package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.example.demo.enums.RoleEnum;
import com.example.demo.enums.workflow.WFAssigneeRoleEnum;
import com.example.demo.enums.workflow.WFProcessesEnum;
import com.example.demo.enums.workflow.WFProductStatusEnum;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.User;
import com.example.demo.model.orm.workflow.ProductTransactionHistory;
import com.example.demo.repository.OrderDetailsRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.workflow.ProductTransactionHistoryRepository;
import com.example.demo.repository.workflow.WFInstanceRepository;
import com.example.demo.repository.workflow.WFProductRepository;
import com.example.demo.repository.workflow.WFTaskDetailsRepository;
import com.example.demo.repository.workflow.WFTaskRepository;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ProductServiceUnitTest {
 
    @InjectMocks
    public ProductServiceImpl productService;

    @MockBean
    public WFProductServiceImpl wfProductService;
    
    @MockBean
    public UserServiceImpl userService;
	   
    @MockBean
    private ProductRepository productRepository;
    
    @MockBean
    private OrderDetailsRepository orderDetailsRepository;

    @MockBean
    private OrderRepository orderRepository;
    
    @MockBean
    public WFProductRepository wfProductRepository;
    
    @MockBean 
    WFTaskDetailsRepository wfTaskDetailsRepository;

    @MockBean
    WFInstanceRepository wfInstanceRepository;
    
    @MockBean 
    WFTaskRepository wfTaskRepository;
    
    @MockBean
    UserRepository userRepository;
    
    @MockBean
    ProductTransactionHistoryRepository productTransactionHistoryRepository;
    
    @Captor
    ArgumentCaptor<Product> productCaptor;
    
    @Captor
    ArgumentCaptor<Long> longCaptor;
    
    @Captor
    ArgumentCaptor<WFProductStatusEnum> wfProductStatusEnumCaptor;
    
    @Captor
    ArgumentCaptor<Boolean> booleanCaptor;
    
    @Captor
    ArgumentCaptor<ProductTransactionHistory> productTransactionHistoryCaptor;

    @Captor
    ArgumentCaptor<String> stringCaptor;
    
    @Captor
    ArgumentCaptor<WFAssigneeRoleEnum> wfAssigneeRoleEnumcaptor;
    
    @Captor
    ArgumentCaptor<WFProcessesEnum> wfProcessesEnumCaptor;
    
    @Captor
    ArgumentCaptor<WFAssigneeRoleEnum> wfAssigneeRoleEnumCaptor;
    
    Long superAdminId;
	Long adminId;
	Long customerId;
	// Updated product variables
	String updatedColor;
	Double updatedPrice;
	Integer updatedStockQuantity;
	Product product;
	Product updatedProduct;
	Long instanceId;
	Long wfProductId;
	Long taskId; 
	Long productId;
	Long productTransactionHistoryId;
	
    
    @BeforeEach
    public void init() {
    	superAdminId = 1L;
    	adminId = 2L;
    	customerId = 3L;
    	
    	// Updated product variables
    	updatedColor = "Cyan";
    	updatedPrice = 320.0;
    	updatedStockQuantity = 33;
    	
    	// wf ids
    	instanceId = 1L;
    	wfProductId = 1L;
    	taskId = 1L; 
    	productId = 1L;
    	productTransactionHistoryId = 1L;
    	
    }
    
    
    
    /*
     * 
     * Update Product Tests
     * 
     */
    
    @Test
    public void productUpdateAsValidSuperAdmin() {    	    	
    	// mock products
    	mockProductsForUpdate();
    	
    	// mock super admin
    	mockUser(superAdminId,RoleEnum.SUPER_ADMIN);
        
    	// act
        productService.save(updatedProduct,superAdminId);
        
        // arguments capture
        verify(productRepository,times(1)).save(productCaptor.capture());
        
        // assertions
        assertEquals(productCaptor.getValue(),updatedProduct);

    }
    
    @Test
    public void productUpdateAsValidAdmin() {
    	mockProductsForUpdate();
    	// mock admin and superadmin
    	mockUser(adminId,RoleEnum.ADMIN);
    	mockSuperAdmins(superAdminId);
        
        // Act
        productService.save(updatedProduct,adminId);
        
        // argument capture
        verify(productRepository,times(0)).save(any());
        verify(wfProductService,times(1)).initWorkflowObjects(productCaptor.capture(), longCaptor.capture(), wfProcessesEnumCaptor.capture(), wfProductStatusEnumCaptor.capture(), wfAssigneeRoleEnumCaptor.capture(), longCaptor.capture());
    	// Captured arguments for createWFInstance
    	WFProcessesEnum capturedProcess = wfProcessesEnumCaptor.getValue(); 
    	
    	Long capturedLoginId = longCaptor.getAllValues().get(0); 
    	Long capturedAssigneeId = longCaptor.getAllValues().get(1);
 
    	WFAssigneeRoleEnum wfAssigneeRoleEnum = wfAssigneeRoleEnumCaptor.getValue();
    	// Captured arguments for createWFProduct
    	Product capturedProduct = productCaptor.getValue(); 
    	WFProductStatusEnum capturedWFProductStatus = wfProductStatusEnumCaptor.getValue(); 
    	
    	// assertions
    	assertEquals(wfAssigneeRoleEnum,WFAssigneeRoleEnum.SUPERADMIN);
    	assertEquals(capturedProcess,WFProcessesEnum.UPDATE_PRODUCT);
    	assertEquals(capturedProduct,updatedProduct);
    	assertEquals(capturedWFProductStatus,WFProductStatusEnum.UPDATED);
    	assertEquals(capturedAssigneeId,superAdminId);
    	assertEquals(capturedLoginId,adminId);
    }
    
    @Test
    public void productUpdateAsInvalidCustomer() {
    	mockProductsForUpdate();
    	// mock users including superadmin
    	mockUser(customerId,RoleEnum.CUSTOMER);
    	mockSuperAdmins(superAdminId);     
        // Act
        assertThrows(BusinessException.class,()-> productService.save(updatedProduct,customerId));
       
    }
    
    @Test
    public void productUpdateTwiceAsValidAdmin() {
    	mockProductsForUpdate();
    	// mock admin and superadmin
    	mockUser(adminId,RoleEnum.ADMIN);
    	mockSuperAdmins(superAdminId);
    	when(wfProductService.hasOtherRunningTasks(productId)).thenReturn(true);
        // Act
        assertThrows(BusinessException.class,()-> productService.save(updatedProduct,adminId));
    }
 
    /*
     * 
     * Update Product Quantity Tests
     * 
     */
    
    @Test
    public void productUpdateQuantityAsValidSuperAdmin() {
    	// mock products
    	mockProductForQuantityUpdate();
    	// mock admin and super admin
    	User superAdmin = new User("John", "Doe", RoleEnum.SUPER_ADMIN.getCode(), "password123", "john.doe@example.com", 
                "123 Main St", "1234567890", "American", "Male", 
                new Date(), new Date());
        when(userService.getUserById(superAdminId)).thenReturn(Optional.of(superAdmin));
        
        // act
        productService.updateProductQuantity(product.getId(), updatedStockQuantity, superAdminId);
        
        // arguments capture
        verify(productRepository,times(1)).save(productCaptor.capture());
        
        // assertions
        assertEquals(productCaptor.getValue(),updatedProduct);
    }
    
    @Test
    public void productUpdateQuantityAsValidAdmin() {
    	// mock products
    	mockProductForQuantityUpdate();
    	// mock users including superadmin
    	mockUser(adminId,RoleEnum.ADMIN);
    	mockSuperAdmins(superAdminId);
        // Act
        productService.updateProductQuantity(product.getId(), updatedStockQuantity, adminId);
        // argument capture
        verify(productRepository,times(0)).save(any());
    }


    @Test
    public void productUpdateQuantityAsInvalidCustomer() {
    	// mock products
    	mockProductForQuantityUpdate();
    	// mock customer and superadmin
    	mockUser(customerId,RoleEnum.CUSTOMER);
    	mockSuperAdmins(superAdminId);
        // Act
        assertThrows(BusinessException.class,()->productService.updateProductQuantity(product.getId(), updatedStockQuantity, customerId));
       
    }
    
    @Test
    public void productUpdateQuantityTwiceAsValidAdmin() {
    	// mock products
    	mockProductForQuantityUpdate();
    	// mock users including superadmin
    	mockUser(adminId,RoleEnum.ADMIN);
    	mockSuperAdmins(superAdminId);
    	when(wfProductService.hasOtherRunningTasks(productId)).thenReturn(true);

    	// Act
        assertThrows(BusinessException.class,()->productService.updateProductQuantity(product.getId(), updatedStockQuantity, adminId));

        // argument capture
        verify(productRepository,times(0)).save(any());
    }

    
    /*
     * 
     * Delete Product Tests
     * 
     */
    
    @Test
    public void productDeleteAsSuperAdmin() {    	
    	
    	// mock products
    	mockProductForDelete();
    	// mock super admin
    	mockUser(superAdminId,RoleEnum.SUPER_ADMIN);
        // act
        productService.deleteProduct(product.getId(),superAdminId);
        
        // arguments capture
        verify(productRepository,times(1)).deleteById(any());
        
    }

    @Test
    public void productDeleteAsAdmin() {
    	// mock products
    	mockProductForDelete();
    	// mock admin and super admin
    	mockUser(adminId,RoleEnum.ADMIN);
    	mockSuperAdmins(superAdminId);

        // act
        productService.deleteProduct(product.getId(),adminId);
        

    	// verify delete doesn't happen via repository
        verify(productRepository,times(0)).deleteById(any());
        verify(wfProductService,times(1)).initWorkflowObjects(productCaptor.capture(), longCaptor.capture(), wfProcessesEnumCaptor.capture(), wfProductStatusEnumCaptor.capture(), wfAssigneeRoleEnumCaptor.capture(), longCaptor.capture());
    	// Captured arguments for createWFInstance
    	WFProcessesEnum capturedProcess = wfProcessesEnumCaptor.getValue(); 
    	
    	Long capturedLoginId = longCaptor.getAllValues().get(0); 
    	Long capturedAssigneeId = longCaptor.getAllValues().get(1);
 
    	WFAssigneeRoleEnum wfAssigneeRoleEnum = wfAssigneeRoleEnumCaptor.getValue();
    	// Captured arguments for createWFProduct
    	Product capturedProduct = productCaptor.getValue(); 
    	WFProductStatusEnum capturedWFProductStatus = wfProductStatusEnumCaptor.getValue(); 
    	
    	// assertions
    	assertEquals(wfAssigneeRoleEnum,WFAssigneeRoleEnum.SUPERADMIN);
    	assertEquals(capturedProcess,WFProcessesEnum.DELETE_PRODUCT);
    	assertEquals(capturedProduct,product);
    	assertEquals(capturedWFProductStatus,WFProductStatusEnum.DELETED);
    	assertEquals(capturedAssigneeId,superAdminId);
    	assertEquals(capturedLoginId,adminId);
    }
    
    @Test
    public void productDeleteAsInvalidCustomer() {
    	// mock customer and superadmin
    	mockUser(customerId,RoleEnum.CUSTOMER);
    	mockSuperAdmins(superAdminId);
    	
    	// mock products
    	mockProductForDelete();
    
        // act
        assertThrows(BusinessException.class,()->productService.deleteProduct(product.getId(),customerId));
    }
    

    @Test
    public void productDeleteTwiceAsAdmin() {
    	// mock products
    	mockProductForDelete();
    	// mock admin and super admin
    	mockUser(adminId,RoleEnum.ADMIN);
    	mockSuperAdmins(superAdminId);
    	when(wfProductService.hasOtherRunningTasks(productId)).thenReturn(true);
        // act
        assertThrows(BusinessException.class,()->productService.deleteProduct(product.getId(),adminId));

    }
    
    
    /*
	 * 
	 * HELPER FUNCTIONS
	 * 
	 */
    
    private Product dummyProduct() {
        return Product.builder().id(productId).productName("Laptop").price(1200.00).color("Silver").stockQuantity(50)
                .description("High-performance laptop").build();
    }
    
    private Product dummyUpdatedProduct() {
        return Product.builder().id(productId).productName("Laptop").price(updatedPrice).color(updatedColor).stockQuantity(updatedStockQuantity)
                .description("High-performance laptop").build();
    }
	private void mockUser(Long userId, RoleEnum role) {
		// mock admin and super admin
    	User user = new User("John", "Doe", role.getCode(), "password123", "john.doe@example.com", 
                "123 Main St", "1234567890", "American", "Male", 
                new Date(), new Date());
    	user.setId(userId);        
    	when(userService.getUserById(userId)).thenReturn(Optional.of(user));
	}
	private void mockSuperAdmins(Long userId) {
		User user = new User("John", "Doe", RoleEnum.SUPER_ADMIN.getCode(), "password123", "john.doe@example.com", 
                "123 Main St", "1234567890", "American", "Male", 
                new Date(), new Date());
		user.setId(userId);
		when(userService.findRandomSuperAdminId()).thenReturn(user.getId());
	}

	private void mockProductsForUpdate() {
		product = dummyProduct();
    	updatedProduct = dummyUpdatedProduct();
    	when(productRepository.save(updatedProduct)).thenReturn(updatedProduct);
    	when(productRepository.findById(productId)).thenReturn(Optional.of(product));
	}
	
	private void mockProductForQuantityUpdate() {
		product = dummyProduct();
    	updatedProduct = dummyProduct();
    	updatedProduct.setStockQuantity(updatedStockQuantity);
    	when(productRepository.save(updatedProduct)).thenReturn(updatedProduct);
    	when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
	}

	private void mockProductForDelete() {
		product = dummyProduct();
    	doNothing().when(productRepository).deleteById(any());
    	when(productRepository.findById(any())).thenReturn(Optional.of(product));
	}
    
    

}
