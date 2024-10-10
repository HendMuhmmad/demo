package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
 
    @MockBean
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
    ArgumentCaptor<WFProductStatusEnum> wfProductStatusEnumCaptor;
    
    @Captor
    ArgumentCaptor<Boolean> booleanCaptor;
    
    @Captor
    ArgumentCaptor<ProductTransactionHistory> productTransactionHistoryCaptor;

    @Captor
    ArgumentCaptor<String> stringCaptor;
    
    @Captor
    ArgumentCaptor<WFAssigneeRoleEnum> wfAssigneeRoleEnumcaptor;
    
	Long superAdmin1Id;
	Long superAdmin2Id;
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
    	superAdmin1Id = 1L;
    	superAdmin2Id = 4L;
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
     * Approve Update Tests
     * 
     */
    
    @Test
    public void approveUpdateAsValidSuperAdmin() {
    	mockProductsForUpdate();
    	mockUser(superAdmin1Id,RoleEnum.SUPER_ADMIN);
        // mock wf objects
    	mockGetWFObjectsForUpdate(adminId, instanceId, wfProductId, superAdmin1Id, updatedProduct,taskId);
    	when(productTransactionHistoryRepository.save(any())).thenReturn(dummyUpdateHistory(productTransactionHistoryId, product, updatedColor, updatedPrice, updatedStockQuantity));
    	
        // act
        wfProductService.respondToTask(taskId, superAdmin1Id, "approve", "note", null);
        
        // arguments capture
        verify(productService,times(1)).save(productCaptor.capture(),any());
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
    	mockProductsForUpdate();
    	// mock wf objects
    	mockGetWFObjectsForUpdate(adminId, instanceId, wfProductId, superAdmin1Id, updatedProduct,taskId);
    	when(productTransactionHistoryRepository.save(any())).thenReturn(dummyUpdateHistory(productTransactionHistoryId, product, updatedColor, updatedPrice, updatedStockQuantity));
    	
    	// mock 2 super admin
    	mockUser(superAdmin1Id,RoleEnum.SUPER_ADMIN);
    	mockUser(superAdmin2Id,RoleEnum.SUPER_ADMIN);

        // act
        assertThrows(BusinessException.class,()->wfProductService.respondToTask(taskId, superAdmin2Id, "approve", "note", null));
    }

    @Test
    public void approveUpdateAsInvalidAdmin() {
    	// mock products
    	mockProductsForUpdate();
    	// mock wf objects
    	mockGetWFObjectsForUpdate(adminId, instanceId, wfProductId, superAdmin1Id, product,taskId);
    	// mock users
    	mockUser(adminId,RoleEnum.ADMIN);
        // act
        assertThrows(BusinessException.class,() -> wfProductService.respondToTask(taskId, adminId, "approve", "note", null));
        
        // arguments capture
        verify(productRepository,times(0)).save(any());
        
    }
    
    @Test
    public void approveUpdateAsInvalidCustomer() {
    	// mock products
    	mockProductsForUpdate();
      	// mock admin and super admin
    	mockUser(superAdmin1Id,RoleEnum.SUPER_ADMIN);
    	mockUser(customerId,RoleEnum.CUSTOMER);
    	
        // mock wf objects
    	mockGetWFObjectsForUpdate(adminId, instanceId, wfProductId, superAdmin1Id, product,taskId);

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

    	// mock products
    	mockProductsForUpdate();

    	// mock wf objects
    	mockGetWFObjectsForUpdate(adminId, instanceId, wfProductId, superAdmin1Id, product,taskId);
    	mockUser(superAdmin1Id,RoleEnum.SUPER_ADMIN);
        
        // act
        wfProductService.respondToTask(taskId, superAdmin1Id, "reject", "note", "not good enough");
        
        // arguments capture
        verify(productRepository,times(0)).save(any());
    }
    
    @Test
    public void rejectUpdateAsInvalidAdmin() {
    	// mock products
    	mockProductsForUpdate();

    	// mock wf objects
    	mockGetWFObjectsForUpdate(adminId, instanceId, wfProductId, superAdmin1Id, product,taskId);
    	// mock admin and super admin
    	mockUser(adminId,RoleEnum.ADMIN);
        
        // act
        assertThrows(BusinessException.class,() -> wfProductService.respondToTask(taskId, adminId, "reject", "note", "rejection note"));
        
        // arguments capture
        verify(productRepository,times(0)).save(any());
    }
    
    @Test
    public void rejectUpdateAsInvalidCustomer() {
    	mockProductsForUpdate();
      	// mock admin and super admin
    	mockUser(superAdmin1Id,RoleEnum.SUPER_ADMIN);
    	mockUser(customerId,RoleEnum.CUSTOMER);
    	
        // mock wf objects
    	mockGetWFObjectsForUpdate(adminId, instanceId, wfProductId, superAdmin1Id, product,taskId);

        // act
        assertThrows(BusinessException.class,() -> wfProductService.respondToTask(taskId, customerId, "reject", "note", "not suitable"));
        
        // arguments capture
        verify(productRepository,times(0)).save(any());
    }
    
    @Test
    public void rejectUpdateAsIncorrectSuperAdmin() {
    	mockProductsForUpdate();
    	// mock wf objects
    	mockGetWFObjectsForUpdate(adminId, instanceId, wfProductId, superAdmin1Id, updatedProduct,taskId);
    	when(productTransactionHistoryRepository.save(any())).thenReturn(dummyUpdateHistory(productTransactionHistoryId, product, updatedColor, updatedPrice, updatedStockQuantity));
    	
    	// mock 2 super admins
    	mockUser(superAdmin1Id,RoleEnum.SUPER_ADMIN);
    	mockUser(superAdmin2Id,RoleEnum.SUPER_ADMIN);
        
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
    	mockProductsForDelete();
    	// mock wf objects
    	mockGetWFObjectsForDelete(adminId, instanceId, wfProductId, superAdmin1Id, product,taskId);
    	when(productTransactionHistoryRepository.save(any())).thenReturn(dummyDeleteHistory(productTransactionHistoryId, product));
    	
    	// mock admin and super admin
    	mockUser(superAdmin1Id,RoleEnum.SUPER_ADMIN);
        
        // act
        wfProductService.respondToTask(taskId, superAdmin1Id, "approve", "note", null);
        
        // arguments capture
        verify(productService,times(1)).deleteProduct(longCaptor.capture(),any());
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
    	mockProductsForDelete();
    	// mock wf objects
    	mockGetWFObjectsForDelete(adminId, instanceId, wfProductId, superAdmin1Id, product,taskId);
    	when(productTransactionHistoryRepository.save(any())).thenReturn(dummyDeleteHistory(productTransactionHistoryId, product));
    	
    	// mock admin and super admin
    	mockUser(superAdmin1Id,RoleEnum.SUPER_ADMIN);
    	mockUser(adminId,RoleEnum.ADMIN);
        
        // act
        assertThrows(BusinessException.class,() -> wfProductService.respondToTask(taskId, adminId, "approve", "note", null));
        // arguments capture
        verify(productRepository,times(0)).deleteById(any());
        
    }
    
    @Test
    public void approveDeleteAsInvalidCustomer() {
    	// mock product
    	mockProductsForDelete();
    	// mock wf objects
    	mockGetWFObjectsForDelete(customerId, instanceId, wfProductId, superAdmin1Id, product,taskId);
    	when(productTransactionHistoryRepository.save(any())).thenReturn(dummyDeleteHistory(productTransactionHistoryId, product));
    	
    	// mock super admin and customer
    	mockUser(superAdmin1Id,RoleEnum.SUPER_ADMIN);
    	mockUser(customerId,RoleEnum.CUSTOMER);
    	
        // act
        assertThrows(BusinessException.class,() -> wfProductService.respondToTask(taskId, customerId, "approve", "note", null));
        // arguments capture
        verify(productRepository,times(0)).deleteById(any());
        
    }
    
    @Test
    public void approveDeleteAsIncorrectSuperAdmin() {
    	mockProductsForDelete();
    	// mock wf objects
    	mockGetWFObjectsForDelete(adminId, instanceId, wfProductId, superAdmin1Id, product,taskId);
    	when(productTransactionHistoryRepository.save(any())).thenReturn(dummyUpdateHistory(productTransactionHistoryId, product, updatedColor, updatedPrice, updatedStockQuantity));
    	
    	// mock admin and super admin
    	mockUser(superAdmin1Id,RoleEnum.SUPER_ADMIN);
    	mockUser(superAdmin2Id,RoleEnum.SUPER_ADMIN);
        
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
    	// mock products
    	mockProductsForDelete();
    	// mock wf objects
    	mockGetWFObjectsForDelete(adminId, instanceId, wfProductId, superAdmin1Id, product,taskId);
    	// mock super admin
    	mockUser(superAdmin1Id,RoleEnum.SUPER_ADMIN);
        // act
        wfProductService.respondToTask(taskId, superAdmin1Id, "reject", "note", "not good enough");
        
        // arguments capture
        verify(productRepository,times(0)).deleteById(any());
    }
    
    @Test
    public void rejectDeleteAsInvalidAdmin() {
    	// mock products
    	mockProductsForDelete();

    	// mock wf objects
    	mockGetWFObjectsForDelete(adminId, instanceId, wfProductId, superAdmin1Id, product,taskId);
    	// mock admin and super admin
    	mockUser(adminId,RoleEnum.ADMIN);
        
        // act
        assertThrows(BusinessException.class,() -> wfProductService.respondToTask(taskId, adminId, "reject", "note", "rejection note"));
        
        // arguments capture
        verify(productRepository,times(0)).save(any());
    }
    
    @Test
    public void rejectDeleteAsInvalidCustomer() {
    	// mock products
    	mockProductsForDelete();

    	// mock customer and super admin
    	mockUser(superAdmin1Id,RoleEnum.SUPER_ADMIN);
    	mockUser(customerId,RoleEnum.CUSTOMER);
    	
        // mock wf objects
    	mockGetWFObjectsForDelete(adminId, instanceId, wfProductId, superAdmin1Id, product,taskId);

        // act
        assertThrows(BusinessException.class,() -> wfProductService.respondToTask(taskId, customerId, "reject", "note", "not suitable"));
        
        // arguments capture
        verify(productRepository,times(0)).save(any());
    }

    @Test
    public void rejectDeleteAsIncorrectSuperAdmin() {
    	mockProductsForDelete();
    	// mock wf objects
    	mockGetWFObjectsForDelete(adminId, instanceId, wfProductId, superAdmin1Id, product,taskId);
    	when(productTransactionHistoryRepository.save(any())).thenReturn(dummyUpdateHistory(productTransactionHistoryId, product, updatedColor, updatedPrice, updatedStockQuantity));
    	
    	// mock admin and super admin
    	mockUser(superAdmin1Id,RoleEnum.SUPER_ADMIN);
    	mockUser(superAdmin2Id,RoleEnum.SUPER_ADMIN);

        
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
	

	private void mockUser(Long userId, RoleEnum role) {
		// mock admin and super admin
    	User user = new User("John", "Doe", role.getCode(), "password123", "john.doe@example.com", 
                "123 Main St", "1234567890", "American", "Male", 
                new Date(), new Date());
    	user.setId(userId);        
    	when(userService.getUserById(userId)).thenReturn(Optional.of(user));
	}

	private void mockProductsForUpdate() {
    	product = dummyProduct();
		updatedProduct = Product.builder().id(productId).productName("Laptop").price(updatedPrice).color(updatedColor).stockQuantity(updatedStockQuantity)
                .description("High-performance laptop").build();
    	// mock product
    	when(productRepository.save(updatedProduct)).thenReturn(updatedProduct);
        when(productService.findById(productId)).thenReturn(product);
        
	}
	private void mockProductsForDelete() {
    	product = dummyProduct();
        when(productService.findById(productId)).thenReturn(product);
    	doNothing().when(productRepository).deleteById(any());
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
