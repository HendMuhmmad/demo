package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

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
import com.example.demo.enums.workflow.WFInstanceStatusEnum;
import com.example.demo.enums.workflow.WFProcessesEnum;
import com.example.demo.enums.workflow.WFProductStatusEnum;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.User;
import com.example.demo.model.orm.workflow.ProductTransactionHistory;
import com.example.demo.model.orm.workflow.WFInstance;
import com.example.demo.model.orm.workflow.WFProduct;
import com.example.demo.model.orm.workflow.WFTask;
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
public class ProductWorkflowServiceUnitTest {
 
    @InjectMocks
    public ProductServiceImpl productService;

    @InjectMocks
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
    ArgumentCaptor<Boolean> booleanCaptor;
    
    @Captor
    ArgumentCaptor<ProductTransactionHistory> productTransactionHistoryCaptor;

    @Captor
    ArgumentCaptor<WFProductStatusEnum> wfProductStatusEnumCaptor;
    
    @Captor
    ArgumentCaptor<String> stringCaptor;
    
    /*
     * 
     * Update Product Tests
     * 
     */
    
    @Test
    public void productUpdateAsValidSuperAdmin() {
    	Long superAdminId = 1L;
    	
    	// partial mock of productService
    	productService = spy(productService);
    	
    	// mock products
    	Product product = dummyProduct();
    	when(productRepository.save(product)).thenReturn(product);
    	
    	// mock admin and super admin
    	User superAdmin = new User("John", "Doe", RoleEnum.SUPER_ADMIN.getCode(), "password123", "john.doe@example.com", 
                "123 Main St", "1234567890", "American", "Male", 
                new Date(), new Date());
        when(userService.getUserById(superAdminId)).thenReturn(Optional.of(superAdmin));
        
        // act
        productService.save(product,superAdminId,false);
        
        // arguments capture
        verify(productService).saveAsSuperAdmin(productCaptor.capture(),longCaptor.capture());
        verify(productRepository,times(1)).save(any());
        
        // assertions
        assertEquals(productCaptor.getValue(),product);
        assertEquals(longCaptor.getValue(),Long.valueOf(1L));
    }
    
    @Test
    public void productUpdateAsValidAdmin() {
    	// id variables
    	Long adminId = 2L;
    	Long instanceId = 2L;
    	Long wfProductId = 1L;
    	Long superAdminId = 2L;
    	
    	// partial mock of productService
    	productService = spy(productService);
    	
    	// mock products
    	Product product = dummyProduct();
    	when(productRepository.save(product)).thenReturn(product);
    	
    	// mock users including superadmin
    	User admin = new User("John", "Doe", RoleEnum.ADMIN.getCode(), "password123", "john.doe@example.com", 
                "123 Main St", "1234567890", "American", "Male", 
                new Date(), new Date());
    	User superAdmin = new User("John", "Doe", RoleEnum.SUPER_ADMIN.getCode(), "password123", "john.doe@example.com", 
                "123 Main St", "1234567890", "American", "Male", 
                new Date(), new Date());
    	superAdmin.setId(superAdminId);
        when(userService.getUserById(adminId)).thenReturn(Optional.of(admin));
        when(userRepository.findByRoleId(RoleEnum.SUPER_ADMIN.getCode())).thenReturn(Arrays.asList(superAdmin));
        
        // mock wf objects
        mockSaveWFObjectsForUpdate(adminId, instanceId, wfProductId, superAdminId, product);
        // Act
        productService.save(product,adminId,false);
        
        // argument capture
        verify(productService).saveAsAdmin(productCaptor.capture(),longCaptor.capture(),booleanCaptor.capture());
        verify(productRepository,times(0)).save(any());
        verify(productService,times(1)).createWFInstance(longCaptor.capture(), longCaptor.capture());
    	verify(productService,times(1)).createWFProduct(productCaptor.capture(), longCaptor.capture(),wfProductStatusEnumCaptor.capture());
    	verify(productService,times(1)).createWFTask(longCaptor.capture(), longCaptor.capture(), stringCaptor.capture());
        
    	// Captured arguments for createWFInstance
    	Long capturedProcessId = longCaptor.getAllValues().get(0); 
    	Long capturedRequesterId = longCaptor.getAllValues().get(1);

    	// Captured arguments for createWFProduct
    	Product capturedProduct = productCaptor.getValue(); 
    	Long capturedInstanceIdForProduct = longCaptor.getAllValues().get(2); 
    	WFProductStatusEnum capturedWFProductStatus = wfProductStatusEnumCaptor.getValue(); 

    	// Captured arguments for createWFTask
    	Long capturedInstanceIdForTask = longCaptor.getAllValues().get(3); 
    	Long capturedAssigneeId = longCaptor.getAllValues().get(4);
    	String capturedAssigneeRole = stringCaptor.getValue(); 
    	assertEquals(capturedProcessId.longValue(),WFProcessesEnum.UPDATE_PRODUCT.getCode());
    	assertEquals(capturedRequesterId,adminId);
    	assertEquals(capturedProduct,product);
    	assertEquals(capturedInstanceIdForProduct,instanceId);
    	assertEquals(capturedWFProductStatus,WFProductStatusEnum.UPDATED);
    	assertEquals(capturedInstanceIdForTask,instanceId);
    	assertEquals(capturedAssigneeId,superAdminId);
    	assertEquals(capturedAssigneeRole,WFAssigneeRoleEnum.SUPERADMIN.getRole());
    	
        // assertions
        assertEquals(productCaptor.getValue(),product);
        assertEquals(longCaptor.getValue(),Long.valueOf(adminId));
        assertEquals(booleanCaptor.getValue(),false);
    }
    
    @Test
    public void productUpdateAsInvalidCustomer() {
    	// id variables
    	Long customerId = 2L;
    	Long instanceId = 1L;
    	Long wfProductId = 1L;
    	Long superAdminId = 2L;
    	
    	// partial mock of productService
    	productService = spy(productService);
    	
    	// mock products
    	Product product = dummyProduct();
    	when(productRepository.save(product)).thenReturn(product);
    	
    	// mock users including superadmin
    	User customer = new User("John", "Doe", RoleEnum.CUSTOMER.getCode(), "password123", "john.doe@example.com", 
                "123 Main St", "1234567890", "American", "Male", 
                new Date(), new Date());

        when(userService.getUserById(customerId)).thenReturn(Optional.of(customer));        
        // mock wf objects
        mockSaveWFObjectsForUpdate(customerId, instanceId, wfProductId, superAdminId, product);
        // Act
        assertThrows(BusinessException.class,()-> productService.save(product,customerId,false));
       
    }
    
    @Test
    public void productUpdateQuantityAsValidSuperAdmin() {
    	Long superAdminId = 1L;
    	Integer updatedQuantity = 100;

    	// partial mock of productService
    	productService = spy(productService);
    	
    	// mock products
    	Product product = dummyProduct();
    	Product updatedProduct = dummyProduct();
    	updatedProduct.setStockQuantity(updatedQuantity);
    	when(productRepository.save(product)).thenReturn(product);
    	when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

    	// mock admin and super admin
    	User superAdmin = new User("John", "Doe", RoleEnum.SUPER_ADMIN.getCode(), "password123", "john.doe@example.com", 
                "123 Main St", "1234567890", "American", "Male", 
                new Date(), new Date());
        when(userService.getUserById(superAdminId)).thenReturn(Optional.of(superAdmin));
        
        // act
        productService.updateProductQuantity(product.getId(), updatedQuantity, superAdminId);
        
        // arguments capture
        verify(productService).saveAsSuperAdmin(productCaptor.capture(),longCaptor.capture());
        verify(productRepository,times(1)).save(any());
        
        // assertions
        assertEquals(productCaptor.getValue(),updatedProduct);
        assertEquals(longCaptor.getValue(),Long.valueOf(1L));
    }
    
    @Test
    public void productUpdateQuantityAsValidAdmin() {
    	// id variables
    	Long adminId = 2L;
    	Long instanceId = 1L;
    	Long wfProductId = 1L;
    	Long superAdminId = 2L;
    	Integer updatedQuantity = 100;
    	
    	// partial mock of productService
    	productService = spy(productService);
    	
    	// mock products
    	Product product = dummyProduct();
    	Product updatedProduct = dummyProduct();
    	updatedProduct.setStockQuantity(updatedQuantity);
    	when(productRepository.save(product)).thenReturn(product);
    	when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
    	// mock users including superadmin
    	User admin = new User("John", "Doe", RoleEnum.ADMIN.getCode(), "password123", "john.doe@example.com", 
                "123 Main St", "1234567890", "American", "Male", 
                new Date(), new Date());
    	User superAdmin = new User("John", "Doe", RoleEnum.ADMIN.getCode(), "password123", "john.doe@example.com", 
                "123 Main St", "1234567890", "American", "Male", 
                new Date(), new Date());
    	superAdmin.setId(superAdminId);
        
    	when(userService.getUserById(adminId)).thenReturn(Optional.of(admin));
        
    	when(userRepository.findByRoleId(RoleEnum.SUPER_ADMIN.getCode())).thenReturn(Arrays.asList(superAdmin));
        
        // mock wf objects
        mockSaveWFObjectsForUpdate(adminId, instanceId, wfProductId, superAdminId, product);
        
        // Act
        productService.updateProductQuantity(product.getId(), updatedQuantity, adminId);
        
        // argument capture
        verify(productService).saveAsAdmin(productCaptor.capture(),longCaptor.capture(),booleanCaptor.capture());
        verify(productRepository,times(0)).save(any());
        
        // assertions
        assertEquals(productCaptor.getValue(),updatedProduct);
        assertEquals(longCaptor.getValue(),Long.valueOf(adminId));
        assertEquals(booleanCaptor.getValue(),false);
    }
    
    @Test
    public void productUpdateQuantityAsInvalidCustomer() {
    	// id variables
    	Long customerId = 2L;
    	Integer updatedQuantity = 100;
    	// partial mock of productService
    	productService = spy(productService);
    	
    	// mock products
    	Product product = dummyProduct();
    	when(productRepository.save(product)).thenReturn(product);
    	
    	// mock users including superadmin
    	User customer = new User("John", "Doe", RoleEnum.CUSTOMER.getCode(), "password123", "john.doe@example.com", 
                "123 Main St", "1234567890", "American", "Male", 
                new Date(), new Date());
        when(userService.getUserById(customerId)).thenReturn(Optional.of(customer));
        // Act
        assertThrows(BusinessException.class,()->productService.updateProductQuantity(product.getId(), updatedQuantity, customerId));
       
    }
    
    /*
     * 
     * Delete Product Tests
     * 
     */
    
    @Test
    public void productDeleteAsSuperAdmin() {    	
    	Long superAdminId = 1L;
    	
    	// partial mock of productService
    	productService = spy(productService);
    	
    	// mock products
    	Product product = dummyProduct();
    	doNothing().when(productRepository).deleteById(any());
    	when(productRepository.findById(any())).thenReturn(Optional.of(product));
    	// mock admin and super admin
    	User superAdmin = new User("John", "Doe", RoleEnum.SUPER_ADMIN.getCode(), "password123", "john.doe@example.com", 
                "123 Main St", "1234567890", "American", "Male", 
                new Date(), new Date());
        when(userService.getUserById(superAdminId)).thenReturn(Optional.of(superAdmin));
        
        // act
        productService.deleteProduct(product.getId(),superAdminId);
        
        // arguments capture
        verify(productRepository,times(1)).deleteById(any());
        
    }
    
    @Test
    public void productDeleteAsAdmin() {
    	// mock products
    	Long adminId = 1L;
    	Long instanceId = 1L;
    	Long wfProductId = 1L;
    	Long superAdminId = 2L;
    	// partial mock of productService
    	productService = spy(productService);
    	
    	// mock products
    	Product product = dummyProduct();
    	when(productRepository.findById(any())).thenReturn(Optional.of(product));
    	// mock admin and super admin
    	User admin = new User("John", "Doe", RoleEnum.ADMIN.getCode(), "password123", "john.doe@example.com", 
                "123 Main St", "1234567890", "American", "Male", 
                new Date(), new Date());
        User superAdmin = new User("John", "Doe", RoleEnum.ADMIN.getCode(), "password123", "john.doe@example.com", 
                "123 Main St", "1234567890", "American", "Male", 
                new Date(), new Date());
    	superAdmin.setId(superAdminId);
    	when(userService.getUserById(adminId)).thenReturn(Optional.of(admin));
        when(userRepository.findByRoleId(RoleEnum.SUPER_ADMIN.getCode())).thenReturn(Arrays.asList(superAdmin));
    	
        // mock wf objects
        mockSaveWFObjectsForDelete(adminId, instanceId, wfProductId, superAdminId, product);
        
        // act
        productService.deleteProduct(product.getId(),adminId);
        

    	// verify delete doesn't happen via repository
        verify(productRepository,times(0)).deleteById(any());
    	verify(productService,times(1)).createWFInstance(longCaptor.capture(), longCaptor.capture());
    	verify(productService,times(1)).createWFProduct(productCaptor.capture(), longCaptor.capture(),wfProductStatusEnumCaptor.capture());
    	verify(productService,times(1)).createWFTask(longCaptor.capture(), longCaptor.capture(), stringCaptor.capture());
        
    	// Captured arguments for createWFInstance
    	Long capturedProcessId = longCaptor.getAllValues().get(0); 
    	Long capturedRequesterId = longCaptor.getAllValues().get(1);

    	// Captured arguments for createWFProduct
    	Product capturedProduct = productCaptor.getValue(); 
    	Long capturedInstanceIdForProduct = longCaptor.getAllValues().get(2); 
    	WFProductStatusEnum capturedWFProductStatus = wfProductStatusEnumCaptor.getValue(); 

    	// Captured arguments for createWFTask
    	Long capturedInstanceIdForTask = longCaptor.getAllValues().get(3); 
    	Long capturedAssigneeId = longCaptor.getAllValues().get(4);
    	String capturedAssigneeRole = stringCaptor.getValue(); 
    	assertEquals(capturedProcessId.longValue(),WFProcessesEnum.DELETE_PRODUCT.getCode());
    	assertEquals(capturedRequesterId,adminId);
    	assertEquals(capturedProduct,product);
    	assertEquals(capturedInstanceIdForProduct,instanceId);
    	assertEquals(capturedWFProductStatus,WFProductStatusEnum.DELETED);
    	assertEquals(capturedInstanceIdForTask,instanceId);
    	assertEquals(capturedAssigneeId,superAdminId);
    	assertEquals(capturedAssigneeRole,WFAssigneeRoleEnum.SUPERADMIN.getRole());
    }
    
    @Test
    public void productDeleteAsInvalidCustomer() {
    	// mock users
    	
    	// mock products
    	Long customerId = 1L;
    	
    	// partial mock of productService
    	productService = spy(productService);
    	
    	// mock products
    	Product product = dummyProduct();
    	when(productRepository.findById(any())).thenReturn(Optional.of(product));
    	// mock admin and super admin
    	User customer = new User("John", "Doe", RoleEnum.CUSTOMER.getCode(), "password123", "john.doe@example.com", 
                "123 Main St", "1234567890", "American", "Male", 
                new Date(), new Date());
        when(userService.getUserById(customerId)).thenReturn(Optional.of(customer));        
        // act
        assertThrows(BusinessException.class,()->productService.deleteProduct(product.getId(),customerId));
    }
    
    /*
     * 
     * Approve Update Tests
     * 
     */
    
    @Test
    public void approveUpdateAsValidSuperAdmin() {
    	Long superAdminId = 1L;
    	Long adminId = 2L;
    	Long instanceId = 1L;
    	Long wfProductId = 1L;
    	Long taskId = 1L; 
    	Long productId = 1L;
    	Long productTransactionHistoryId = 1L;
    	Product product = dummyProduct();
    	
    	// Updated product variables
    	String updatedColor = "Cyan";
    	Double updatedPrice = product.getPrice() + 20;
    	Integer updatedStockQuantity = product.getStockQuantity() + 30;
    	Product updatedProduct = Product.builder().id(productId).productName("Laptop").price(updatedPrice).color(updatedColor).stockQuantity(updatedStockQuantity)
                .description("High-performance laptop").build();
    	
    	// mock product
    	product.setId(productId);
    	when(productRepository.save(updatedProduct)).thenReturn(updatedProduct);
    	when(productRepository.findById(productId)).thenReturn(Optional.of(product));

    	// mock wf objects
    	mockGetWFObjectsForUpdate(adminId, instanceId, wfProductId, superAdminId, updatedProduct,taskId);
    	when(productTransactionHistoryRepository.save(any())).thenReturn(dummyUpdateHistory(productTransactionHistoryId, product, updatedColor, updatedPrice, updatedStockQuantity));
    	
    	// mock admin and super admin
    	User superAdmin = new User("John", "Doe", RoleEnum.SUPER_ADMIN.getCode(), "password123", "john.doe@example.com", 
                "123 Main St", "1234567890", "American", "Male", 
                new Date(), new Date());
        when(userService.getUserById(superAdminId)).thenReturn(Optional.of(superAdmin));
        
        // act
        wfProductService.respondToTask(taskId, superAdminId, "approve", "note", null);
        
        // arguments capture
        verify(productRepository,times(1)).save(productCaptor.capture());
        Product updatedCapturedProduct = productCaptor.getValue();
        assertEquals(updatedCapturedProduct.getColor(),updatedColor);
        assertEquals(updatedCapturedProduct.getPrice(),updatedPrice.doubleValue());
        assertEquals(updatedCapturedProduct.getStockQuantity().intValue(),updatedStockQuantity.intValue());        
        
        // verify history
        verify(productTransactionHistoryRepository,times(1)).save(productTransactionHistoryCaptor.capture());
        ProductTransactionHistory history = productTransactionHistoryCaptor.getValue();
        assertEquals(history.getColor(), product.getColor());
        assertEquals(history.getStockQuantity(), product.getStockQuantity());
        assertEquals(history.getPrice().doubleValue(), product.getPrice());
    }
    
    @Test
    public void approveUpdateAsIncorrectSuperAdmin() {
    	Long superAdmin1Id = 1L;
    	Long superAdmin2Id = 2L;
    	Long adminId = 2L;
    	Long instanceId = 1L;
    	Long wfProductId = 1L;
    	Long taskId = 1L; 
    	Long productId = 1L;
    	Long productTransactionHistoryId = 1L;
    	Product product = dummyProduct();
    	
    	// Updated product variables
    	String updatedColor = "Cyan";
    	Double updatedPrice = product.getPrice() + 20;
    	Integer updatedStockQuantity = product.getStockQuantity() + 30;
    	Product updatedProduct = Product.builder().id(productId).productName("Laptop").price(updatedPrice).color(updatedColor).stockQuantity(updatedStockQuantity)
                .description("High-performance laptop").build();
    	
    	// mock product
    	product.setId(productId);
    	when(productRepository.save(updatedProduct)).thenReturn(updatedProduct);
    	when(productRepository.findById(productId)).thenReturn(Optional.of(product));

    	// mock wf objects
    	mockGetWFObjectsForUpdate(adminId, instanceId, wfProductId, superAdmin1Id, updatedProduct,taskId);
    	when(productTransactionHistoryRepository.save(any())).thenReturn(dummyUpdateHistory(productTransactionHistoryId, product, updatedColor, updatedPrice, updatedStockQuantity));
    	
    	// mock admin and super admin
    	User superAdmin1 = new User("John", "Doe", RoleEnum.SUPER_ADMIN.getCode(), "password123", "john.doe@example.com", 
                "123 Main St", "1234567890", "American", "Male", 
                new Date(), new Date());
    	User superAdmin2 = new User("John", "Doe", RoleEnum.SUPER_ADMIN.getCode(), "password123", "john.doe@example.com", 
                "123 Main St", "1234567890", "American", "Male", 
                new Date(), new Date());
        when(userService.getUserById(superAdmin1Id)).thenReturn(Optional.of(superAdmin1));
        when(userService.getUserById(superAdmin2Id)).thenReturn(Optional.of(superAdmin2));
        
        // act
        assertThrows(BusinessException.class,()->wfProductService.respondToTask(taskId, superAdmin2Id, "approve", "note", null));
    }

    @Test
    public void approveUpdateAsInvalidAdmin() {
    	Long superAdminId = 1L;
    	Long adminId = 2L;
    	Long instanceId = 1L;
    	Long wfProductId = 1L;
    	Long taskId = 1L; 

    	// partial mock of productService
    	productService = spy(productService);
    	
    	// mock products
    	Product product = dummyProduct();
    	when(productRepository.save(product)).thenReturn(product);
    	when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

    	// mock wf objects
    	mockGetWFObjectsForUpdate(adminId, instanceId, wfProductId, superAdminId, product,taskId);
    	// mock admin and super admin
    	User admin = new User("John", "Doe", RoleEnum.ADMIN.getCode(), "password123", "john.doe@example.com", 
                "123 Main St", "1234567890", "American", "Male", 
                new Date(), new Date());
        when(userService.getUserById(adminId)).thenReturn(Optional.of(admin));
        
        // act
        assertThrows(BusinessException.class,() -> wfProductService.respondToTask(taskId, adminId, "approve", "note", null));
        
        // arguments capture
        verify(productRepository,times(0)).save(any());
        
    }
    
    @Test
    public void approveUpdateAsInvalidCustomer() {
    	Long superAdminId = 1L;
    	Long customerId = 2L;
    	Long adminId = 3L;
    	Long instanceId = 1L;
    	Long wfProductId = 1L;
    	Long taskId = 1L; 

    	// partial mock of productService
    	productService = spy(productService);
    	
    	// mock products
    	Product product = dummyProduct();
    	when(productRepository.save(product)).thenReturn(product);
    	when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

      	// mock admin and super admin
    	User superAdmin = new User("John", "Doe", RoleEnum.SUPER_ADMIN.getCode(), "password123", "john.doe@example.com", 
                "123 Main St", "1234567890", "American", "Male", 
                new Date(), new Date());
    	User customer = new User("John", "Doe", RoleEnum.CUSTOMER.getCode(), "password123", "john.doe@example.com", 
                "123 Main St", "1234567890", "American", "Male", 
                new Date(), new Date());
        when(userService.getUserById(superAdminId)).thenReturn(Optional.of(superAdmin));
        when(userService.getUserById(customerId)).thenReturn(Optional.of(customer));
    	
        // mock wf objects
    	mockGetWFObjectsForUpdate(adminId, instanceId, wfProductId, superAdminId, product,taskId);

        // act
        assertThrows(BusinessException.class,() -> wfProductService.respondToTask(taskId, customerId, "approve", "note", null));
        
        // arguments capture
        verify(productRepository,times(0)).save(any());
    }
    
    
    /*
     * 
     * Reject Update Tests
     * 
     */
    
    @Test
    public void rejectUpdateAsValidSuperAdmin() {
    	Long superAdminId = 1L;
    	Long adminId = 2L;
    	Long instanceId = 1L;
    	Long wfProductId = 1L;
    	Long taskId = 1L; 

    	// partial mock of productService
    	productService = spy(productService);
    	
    	// mock products
    	Product product = dummyProduct();
    	when(productRepository.save(product)).thenReturn(product);
    	when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

    	// mock wf objects
    	mockGetWFObjectsForUpdate(adminId, instanceId, wfProductId, superAdminId, product,taskId);
    	// mock admin and super admin
    	User superAdmin = new User("John", "Doe", RoleEnum.SUPER_ADMIN.getCode(), "password123", "john.doe@example.com", 
                "123 Main St", "1234567890", "American", "Male", 
                new Date(), new Date());
        when(userService.getUserById(superAdminId)).thenReturn(Optional.of(superAdmin));
        
        // act
        wfProductService.respondToTask(taskId, superAdminId, "reject", "note", "not good enough");
        
        // arguments capture
        verify(productRepository,times(0)).save(any());
    }
    
    @Test
    public void rejectUpdateAsInvalidAdmin() {
    	Long superAdminId = 1L;
    	Long adminId = 2L;
    	Long instanceId = 1L;
    	Long wfProductId = 1L;
    	Long taskId = 1L; 

    	// partial mock of productService
    	productService = spy(productService);
    	
    	// mock products
    	Product product = dummyProduct();
    	when(productRepository.save(product)).thenReturn(product);
    	when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

    	// mock wf objects
    	mockGetWFObjectsForUpdate(adminId, instanceId, wfProductId, superAdminId, product,taskId);
    	// mock admin and super admin
    	User admin = new User("John", "Doe", RoleEnum.ADMIN.getCode(), "password123", "john.doe@example.com", 
                "123 Main St", "1234567890", "American", "Male", 
                new Date(), new Date());
        when(userService.getUserById(adminId)).thenReturn(Optional.of(admin));
        
        // act
        assertThrows(BusinessException.class,() -> wfProductService.respondToTask(taskId, adminId, "reject", "note", "rejection note"));
        
        // arguments capture
        verify(productRepository,times(0)).save(any());
    }
    
    @Test
    public void rejectUpdateAsInvalidCustomer() {
    	Long superAdminId = 1L;
    	Long customerId = 2L;
    	Long adminId = 3L;
    	Long instanceId = 1L;
    	Long wfProductId = 1L;
    	Long taskId = 1L; 

    	// partial mock of productService
    	productService = spy(productService);
    	
    	// mock products
    	Product product = dummyProduct();
    	when(productRepository.save(product)).thenReturn(product);
    	when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

      	// mock admin and super admin
    	User superAdmin = new User("John", "Doe", RoleEnum.SUPER_ADMIN.getCode(), "password123", "john.doe@example.com", 
                "123 Main St", "1234567890", "American", "Male", 
                new Date(), new Date());
    	User customer = new User("John", "Doe", RoleEnum.CUSTOMER.getCode(), "password123", "john.doe@example.com", 
                "123 Main St", "1234567890", "American", "Male", 
                new Date(), new Date());
        when(userService.getUserById(superAdminId)).thenReturn(Optional.of(superAdmin));
        when(userService.getUserById(customerId)).thenReturn(Optional.of(customer));
    	
        // mock wf objects
    	mockGetWFObjectsForUpdate(adminId, instanceId, wfProductId, superAdminId, product,taskId);

        // act
        assertThrows(BusinessException.class,() -> wfProductService.respondToTask(taskId, customerId, "reject", "note", "not suitable"));
        
        // arguments capture
        verify(productRepository,times(0)).save(any());
    }
    
    @Test
    public void rejectUpdateAsIncorrectSuperAdmin() {
    	Long superAdmin1Id = 1L;
    	Long superAdmin2Id = 2L;
    	Long adminId = 2L;
    	Long instanceId = 1L;
    	Long wfProductId = 1L;
    	Long taskId = 1L; 
    	Long productId = 1L;
    	Long productTransactionHistoryId = 1L;
    	Product product = dummyProduct();
    	
    	// Updated product variables
    	String updatedColor = "Cyan";
    	Double updatedPrice = product.getPrice() + 20;
    	Integer updatedStockQuantity = product.getStockQuantity() + 30;
    	Product updatedProduct = Product.builder().id(productId).productName("Laptop").price(updatedPrice).color(updatedColor).stockQuantity(updatedStockQuantity)
                .description("High-performance laptop").build();
    	
    	// mock product
    	product.setId(productId);
    	when(productRepository.save(updatedProduct)).thenReturn(updatedProduct);
    	when(productRepository.findById(productId)).thenReturn(Optional.of(product));

    	// mock wf objects
    	mockGetWFObjectsForUpdate(adminId, instanceId, wfProductId, superAdmin1Id, updatedProduct,taskId);
    	when(productTransactionHistoryRepository.save(any())).thenReturn(dummyUpdateHistory(productTransactionHistoryId, product, updatedColor, updatedPrice, updatedStockQuantity));
    	
    	// mock admin and super admin
    	User superAdmin1 = new User("John", "Doe", RoleEnum.SUPER_ADMIN.getCode(), "password123", "john.doe@example.com", 
                "123 Main St", "1234567890", "American", "Male", 
                new Date(), new Date());
    	User superAdmin2 = new User("John", "Doe", RoleEnum.SUPER_ADMIN.getCode(), "password123", "john.doe@example.com", 
                "123 Main St", "1234567890", "American", "Male", 
                new Date(), new Date());
        when(userService.getUserById(superAdmin1Id)).thenReturn(Optional.of(superAdmin1));
        when(userService.getUserById(superAdmin2Id)).thenReturn(Optional.of(superAdmin2));
        
        // act
        assertThrows(BusinessException.class,()->wfProductService.respondToTask(taskId, superAdmin2Id, "reject", "note", "unsuitable"));
    }

    /*
     * 
     * Approve Delete Tests
     * 
     */
    
    @Test
    public void approveDeleteAsValidSuperAdmin() {
    	Long superAdminId = 1L;
    	Long adminId = 2L;
    	Long instanceId = 1L;
    	Long wfProductId = 1L;
    	Long taskId = 1L; 
    	Long productId = 1L;
    	Long productTransactionHistoryId = 1L;
    	Product product = dummyProduct();
    	
    	// mock product
    	product.setId(productId);
    	when(productRepository.findById(productId)).thenReturn(Optional.of(product));
    	doNothing().when(productRepository).deleteById(any());

    	// mock wf objects
    	mockGetWFObjectsForDelete(adminId, instanceId, wfProductId, superAdminId, product,taskId);
    	when(productTransactionHistoryRepository.save(any())).thenReturn(dummyDeleteHistory(productTransactionHistoryId, product));
    	
    	// mock admin and super admin
    	User superAdmin = new User("John", "Doe", RoleEnum.SUPER_ADMIN.getCode(), "password123", "john.doe@example.com", 
                "123 Main St", "1234567890", "American", "Male", 
                new Date(), new Date());
        when(userService.getUserById(superAdminId)).thenReturn(Optional.of(superAdmin));
        
        // act
        wfProductService.respondToTask(taskId, superAdminId, "approve", "note", null);
        
        // arguments capture
        verify(productRepository,times(1)).deleteById(longCaptor.capture());
        Long capturedId = longCaptor.getValue();
        assertEquals(capturedId, productId);
        
        // verify history
        verify(productTransactionHistoryRepository,times(1)).save(productTransactionHistoryCaptor.capture());
        ProductTransactionHistory history = productTransactionHistoryCaptor.getValue();
        assertEquals(history.getColor(), product.getColor());
        assertEquals(history.getStockQuantity(), product.getStockQuantity());
        assertEquals(history.getPrice().doubleValue(), product.getPrice());
        assertEquals(history.getStatus(),WFProductStatusEnum.DELETED.getCode());
    }

    @Test
    public void approveDeleteAsInvalidAdmin() {
    	Long superAdminId = 1L;
    	Long adminId = 2L;
    	Long instanceId = 1L;
    	Long wfProductId = 1L;
    	Long taskId = 1L; 
    	Long productId = 1L;
    	Long productTransactionHistoryId = 1L;
    	Product product = dummyProduct();
    	
    	// mock product
    	product.setId(productId);
    	when(productRepository.findById(productId)).thenReturn(Optional.of(product));
    	doNothing().when(productRepository).deleteById(any());

    	// mock wf objects
    	mockGetWFObjectsForDelete(adminId, instanceId, wfProductId, superAdminId, product,taskId);
    	when(productTransactionHistoryRepository.save(any())).thenReturn(dummyDeleteHistory(productTransactionHistoryId, product));
    	
    	// mock admin and super admin
    	User superAdmin = new User("John", "Doe", RoleEnum.SUPER_ADMIN.getCode(), "password123", "john.doe@example.com", 
                "123 Main St", "1234567890", "American", "Male", 
                new Date(), new Date());
    	User admin = new User("John", "Doe", RoleEnum.ADMIN.getCode(), "password123", "john.doe@example.com", 
                "123 Main St", "1234567890", "American", "Male", 
                new Date(), new Date());
        when(userService.getUserById(superAdminId)).thenReturn(Optional.of(superAdmin));
        when(userService.getUserById(adminId)).thenReturn(Optional.of(admin));
        
        // act
        assertThrows(BusinessException.class,() -> wfProductService.respondToTask(taskId, adminId, "approve", "note", null));
        // arguments capture
        verify(productRepository,times(0)).deleteById(any());
        
    }
    
    @Test
    public void approveDeleteAsInvalidCustomer() {
    	Long superAdminId = 1L;
    	Long customerId = 2L;
    	Long instanceId = 1L;
    	Long wfProductId = 1L;
    	Long taskId = 1L; 
    	Long productId = 1L;
    	Long productTransactionHistoryId = 1L;
    	Product product = dummyProduct();
    	
    	// mock product
    	product.setId(productId);
    	when(productRepository.findById(productId)).thenReturn(Optional.of(product));
    	doNothing().when(productRepository).deleteById(any());

    	// mock wf objects
    	mockGetWFObjectsForDelete(customerId, instanceId, wfProductId, superAdminId, product,taskId);
    	when(productTransactionHistoryRepository.save(any())).thenReturn(dummyDeleteHistory(productTransactionHistoryId, product));
    	
    	// mock admin and super admin
    	User superAdmin = new User("John", "Doe", RoleEnum.SUPER_ADMIN.getCode(), "password123", "john.doe@example.com", 
                "123 Main St", "1234567890", "American", "Male", 
                new Date(), new Date());
    	User customer = new User("John", "Doe", RoleEnum.CUSTOMER.getCode(), "password123", "john.doe@example.com", 
                "123 Main St", "1234567890", "American", "Male", 
                new Date(), new Date());
        when(userService.getUserById(superAdminId)).thenReturn(Optional.of(superAdmin));
        when(userService.getUserById(customerId)).thenReturn(Optional.of(customer));
        
        // act
        assertThrows(BusinessException.class,() -> wfProductService.respondToTask(taskId, customerId, "approve", "note", null));
        // arguments capture
        verify(productRepository,times(0)).deleteById(any());
        
    }
    
    @Test
    public void approveDeleteAsIncorrectSuperAdmin() {
    	Long superAdmin1Id = 1L;
    	Long superAdmin2Id = 2L;
    	Long adminId = 2L;
    	Long instanceId = 1L;
    	Long wfProductId = 1L;
    	Long taskId = 1L; 
    	Long productId = 1L;
    	Long productTransactionHistoryId = 1L;
    	Product product = dummyProduct();
    	
    	// Updated product variables
    	String updatedColor = "Cyan";
    	Double updatedPrice = product.getPrice() + 20;
    	Integer updatedStockQuantity = product.getStockQuantity() + 30;
    	Product updatedProduct = Product.builder().id(productId).productName("Laptop").price(updatedPrice).color(updatedColor).stockQuantity(updatedStockQuantity)
                .description("High-performance laptop").build();
    	
    	// mock product
    	product.setId(productId);
    	when(productRepository.save(updatedProduct)).thenReturn(updatedProduct);
    	when(productRepository.findById(productId)).thenReturn(Optional.of(product));

    	// mock wf objects
    	mockGetWFObjectsForDelete(adminId, instanceId, wfProductId, superAdmin1Id, updatedProduct,taskId);
    	when(productTransactionHistoryRepository.save(any())).thenReturn(dummyUpdateHistory(productTransactionHistoryId, product, updatedColor, updatedPrice, updatedStockQuantity));
    	
    	// mock admin and super admin
    	User superAdmin1 = new User("John", "Doe", RoleEnum.SUPER_ADMIN.getCode(), "password123", "john.doe@example.com", 
                "123 Main St", "1234567890", "American", "Male", 
                new Date(), new Date());
    	User superAdmin2 = new User("John", "Doe", RoleEnum.SUPER_ADMIN.getCode(), "password123", "john.doe@example.com", 
                "123 Main St", "1234567890", "American", "Male", 
                new Date(), new Date());
        when(userService.getUserById(superAdmin1Id)).thenReturn(Optional.of(superAdmin1));
        when(userService.getUserById(superAdmin2Id)).thenReturn(Optional.of(superAdmin2));
        
        // act
        assertThrows(BusinessException.class,()->wfProductService.respondToTask(taskId, superAdmin2Id, "approve", "note", null));
    }

    /*
     * 
     * Reject Delete Tests
     * 
     */
    
    
    @Test
    public void rejectDeleteAsValidSuperAdmin() {
    	Long superAdminId = 1L;
    	Long adminId = 2L;
    	Long instanceId = 1L;
    	Long wfProductId = 1L;
    	Long taskId = 1L; 

    	// partial mock of productService
    	productService = spy(productService);
    	
    	// mock products
    	Product product = dummyProduct();
    	when(productRepository.save(product)).thenReturn(product);
    	when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

    	// mock wf objects
    	mockGetWFObjectsForDelete(adminId, instanceId, wfProductId, superAdminId, product,taskId);
    	// mock admin and super admin
    	User superAdmin = new User("John", "Doe", RoleEnum.SUPER_ADMIN.getCode(), "password123", "john.doe@example.com", 
                "123 Main St", "1234567890", "American", "Male", 
                new Date(), new Date());
        when(userService.getUserById(superAdminId)).thenReturn(Optional.of(superAdmin));
        
        // act
        wfProductService.respondToTask(taskId, superAdminId, "reject", "note", "not good enough");
        
        // arguments capture
        verify(productRepository,times(0)).deleteById(any());
    }
    
    @Test
    public void rejectDeleteAsInvalidAdmin() {
    	Long superAdminId = 1L;
    	Long adminId = 2L;
    	Long instanceId = 1L;
    	Long wfProductId = 1L;
    	Long taskId = 1L; 

    	// partial mock of productService
    	productService = spy(productService);
    	
    	// mock products
    	Product product = dummyProduct();
    	when(productRepository.save(product)).thenReturn(product);
    	when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

    	// mock wf objects
    	mockGetWFObjectsForDelete(adminId, instanceId, wfProductId, superAdminId, product,taskId);
    	// mock admin and super admin
    	User admin = new User("John", "Doe", RoleEnum.ADMIN.getCode(), "password123", "john.doe@example.com", 
                "123 Main St", "1234567890", "American", "Male", 
                new Date(), new Date());
        when(userService.getUserById(adminId)).thenReturn(Optional.of(admin));
        
        // act
        assertThrows(BusinessException.class,() -> wfProductService.respondToTask(taskId, adminId, "reject", "note", "rejection note"));
        
        // arguments capture
        verify(productRepository,times(0)).save(any());
    }
    
    @Test
    public void rejectDeleteAsInvalidCustomer() {
    	Long superAdminId = 1L;
    	Long customerId = 2L;
    	Long adminId = 3L;
    	Long instanceId = 1L;
    	Long wfProductId = 1L;
    	Long taskId = 1L; 

    	// partial mock of productService
    	productService = spy(productService);
    	
    	// mock products
    	Product product = dummyProduct();
    	when(productRepository.save(product)).thenReturn(product);
    	when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

      	// mock admin and super admin
    	User superAdmin = new User("John", "Doe", RoleEnum.SUPER_ADMIN.getCode(), "password123", "john.doe@example.com", 
                "123 Main St", "1234567890", "American", "Male", 
                new Date(), new Date());
    	User customer = new User("John", "Doe", RoleEnum.CUSTOMER.getCode(), "password123", "john.doe@example.com", 
                "123 Main St", "1234567890", "American", "Male", 
                new Date(), new Date());
        when(userService.getUserById(superAdminId)).thenReturn(Optional.of(superAdmin));
        when(userService.getUserById(customerId)).thenReturn(Optional.of(customer));
    	
        // mock wf objects
    	mockGetWFObjectsForDelete(adminId, instanceId, wfProductId, superAdminId, product,taskId);

        // act
        assertThrows(BusinessException.class,() -> wfProductService.respondToTask(taskId, customerId, "reject", "note", "not suitable"));
        
        // arguments capture
        verify(productRepository,times(0)).save(any());
    }

    @Test
    public void rejectDeleteAsIncorrectSuperAdmin() {
    	Long superAdmin1Id = 1L;
    	Long superAdmin2Id = 2L;
    	Long adminId = 2L;
    	Long instanceId = 1L;
    	Long wfProductId = 1L;
    	Long taskId = 1L; 
    	Long productId = 1L;
    	Long productTransactionHistoryId = 1L;
    	Product product = dummyProduct();
    	
    	// Updated product variables
    	String updatedColor = "Cyan";
    	Double updatedPrice = product.getPrice() + 20;
    	Integer updatedStockQuantity = product.getStockQuantity() + 30;
    	Product updatedProduct = Product.builder().id(productId).productName("Laptop").price(updatedPrice).color(updatedColor).stockQuantity(updatedStockQuantity)
                .description("High-performance laptop").build();
    	
    	// mock product
    	product.setId(productId);
    	when(productRepository.save(updatedProduct)).thenReturn(updatedProduct);
    	when(productRepository.findById(productId)).thenReturn(Optional.of(product));

    	// mock wf objects
    	mockGetWFObjectsForDelete(adminId, instanceId, wfProductId, superAdmin1Id, updatedProduct,taskId);
    	when(productTransactionHistoryRepository.save(any())).thenReturn(dummyUpdateHistory(productTransactionHistoryId, product, updatedColor, updatedPrice, updatedStockQuantity));
    	
    	// mock admin and super admin
    	User superAdmin1 = new User("John", "Doe", RoleEnum.SUPER_ADMIN.getCode(), "password123", "john.doe@example.com", 
                "123 Main St", "1234567890", "American", "Male", 
                new Date(), new Date());
    	User superAdmin2 = new User("John", "Doe", RoleEnum.SUPER_ADMIN.getCode(), "password123", "john.doe@example.com", 
                "123 Main St", "1234567890", "American", "Male", 
                new Date(), new Date());
        when(userService.getUserById(superAdmin1Id)).thenReturn(Optional.of(superAdmin1));
        when(userService.getUserById(superAdmin2Id)).thenReturn(Optional.of(superAdmin2));
        
        // act
        assertThrows(BusinessException.class,()->wfProductService.respondToTask(taskId, superAdmin2Id, "reject", "note", null));
    }

    
    /*
	 * 
	 * HELPER FUNCTIONS
	 * 
	 */
    
	public void mockSaveWFObjectsForUpdate(Long adminId, Long instanceId, Long wfProductId, Long superAdminId, Product product) {
		when(wfInstanceRepository.save(any())).thenReturn(dummyUpdateInstance(adminId, instanceId));
        when(wfProductRepository.save(any())).thenReturn(dummyUpdateWfProduct(product, instanceId, wfProductId));
        when(wfTaskRepository.save(any())).thenReturn(dummyWfTask(instanceId, superAdminId));
	}
	
	public void mockSaveWFObjectsForDelete(Long adminId, Long instanceId, Long wfProductId, Long superAdminId, Product product) {
		when(wfInstanceRepository.save(any())).thenReturn(dummyDeleteInstance(adminId, instanceId));
        when(wfProductRepository.save(any())).thenReturn(dummyDeleteWfProduct(product, instanceId, wfProductId));
        when(wfTaskRepository.save(any())).thenReturn(dummyWfTask(instanceId, superAdminId));
	}

	public void mockGetWFObjectsForUpdate(Long adminId, Long instanceId, Long wfProductId, Long superAdminId, Product updatedProduct, Long taskId) {
		when(wfInstanceRepository.findById(any())).thenReturn(Optional.of(dummyUpdateInstance(adminId, instanceId)));
        when(wfProductRepository.findByWfInstanceId(any())).thenReturn(Optional.of(dummyUpdateWfProduct(updatedProduct, instanceId, wfProductId)));
        when(wfTaskRepository.findById(any())).thenReturn(Optional.of(dummyWfTask(instanceId, superAdminId)));
	}
	public void mockGetWFObjectsForDelete(Long adminId, Long instanceId, Long wfProductId, Long superAdminId, Product product, Long taskId) {
		when(wfInstanceRepository.findById(any())).thenReturn(Optional.of(dummyDeleteInstance(adminId, instanceId)));
        when(wfProductRepository.findByWfInstanceId(any())).thenReturn(Optional.of(dummyDeleteWfProduct(product, instanceId, wfProductId)));
        when(wfTaskRepository.findById(any())).thenReturn(Optional.of(dummyWfTask(instanceId, superAdminId)));
	}
	private WFTask dummyWfTask(Long instanceId, Long superAdminId) {
		return new WFTask(instanceId,superAdminId,WFAssigneeRoleEnum.SUPERADMIN.getRole(),new Date());
	}

	private WFProduct dummyUpdateWfProduct(Product product, Long instanceId, Long wfProductId) {
		return new WFProduct(wfProductId,product.getId(),instanceId,product.getProductName(),product.getPrice(),product.getColor(),product.getStockQuantity(),product.getDescription(),WFProductStatusEnum.UPDATED.getCode());
	}
	
	private WFProduct dummyDeleteWfProduct(Product product, Long instanceId, Long wfProductId) {
		return new WFProduct(wfProductId,product.getId(),instanceId,product.getProductName(),product.getPrice(),product.getColor(),product.getStockQuantity(),product.getDescription(),WFProductStatusEnum.DELETED.getCode());
	}
	
	private ProductTransactionHistory dummyUpdateHistory(Long productTransactionHistoryId, Product product,
			String updatedColor, Double updatedPrice, Integer updatedStockQuantity) {
		return new ProductTransactionHistory(productTransactionHistoryId,product.getId(),product.getProductName(),updatedPrice,updatedColor,updatedStockQuantity,product.getDescription(),new Date(),WFProductStatusEnum.UPDATED.getCode());
	}
    
	private ProductTransactionHistory dummyDeleteHistory(Long productTransactionHistoryId, Product product) {
		return new ProductTransactionHistory(productTransactionHistoryId,product.getId(),product.getProductName(),product.getPrice(),product.getColor(),product.getStockQuantity(),product.getDescription(),new Date(),WFProductStatusEnum.DELETED.getCode());
	}
    

	private WFInstance dummyDeleteInstance(Long adminId, Long instanceId) {
		return new WFInstance(instanceId,WFProcessesEnum.DELETE_PRODUCT.getCode(),adminId,new Date(),WFInstanceStatusEnum.RUNNING.getCode());
	}
	
	private WFInstance dummyUpdateInstance(Long adminId, Long instanceId) {
		return new WFInstance(instanceId,WFProcessesEnum.UPDATE_PRODUCT.getCode(),adminId,new Date(),WFInstanceStatusEnum.RUNNING.getCode());
	}
    
    private Product dummyProduct() {
        return Product.builder().id(1L).productName("Laptop").price(1200.00).color("Silver").stockQuantity(50)
                .description("High-performance laptop").build();
    }

}
