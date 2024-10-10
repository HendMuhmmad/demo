package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.enums.RoleEnum;
import com.example.demo.enums.workflow.WFActionEnum;
import com.example.demo.enums.workflow.WFAssigneeRoleEnum;
import com.example.demo.enums.workflow.WFInstanceStatusEnum;
import com.example.demo.enums.workflow.WFProductStatusEnum;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.User;
import com.example.demo.model.orm.workflow.WFInstance;
import com.example.demo.model.orm.workflow.WFProduct;
import com.example.demo.model.orm.workflow.WFTask;
import com.example.demo.model.orm.workflow.WFTaskDetails;
import com.example.demo.repository.OrderDetailsRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.workflow.WFInstanceRepository;
import com.example.demo.repository.workflow.WFProductRepository;
import com.example.demo.repository.workflow.WFTaskDetailsRepository;
import com.example.demo.repository.workflow.WFTaskRepository;
import com.example.demo.service.workflow.WFProductService;

@SpringBootTest
@ExtendWith(MockitoExtension.class)

public class ProductWorkflowServiceTest {
    @Autowired
    public ProductService productService;

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    public UserService userService;
	
    @Autowired
    public WFProductService wfProductService;
    
    @Autowired
    public WFProductRepository wfProductRepository;
    
    @Autowired 
    WFTaskDetailsRepository wfTaskDetailsRepository;

    @Autowired
    WFInstanceRepository wfInstanceRepository;
    
    @Autowired 
    WFTaskRepository wfTaskRepository;
    
    @Autowired
    UserRepository userRepository;

    private static User testUser;
	private static User testAdmin;
	private static User mytestAdmin;
	private static User testSuperAdmin1;
	private static User testSuperAdmin2;
    private static Product testProduct1;
    private static Product testProduct2;
    private static Product testProduct3;    
    private static WFTask task1;
    private static WFTask task2;
    
    @Transactional
    @BeforeEach
    public void init(@Autowired UserRepository userRepository, 
                            @Autowired ProductRepository productRepository,
                            @Autowired OrderRepository orderRepository, 
                            @Autowired OrderDetailsRepository orderDetailsRepository,    
                            @Autowired WFTaskRepository wfTaskRepository,
                            @Autowired WFProductRepository wfProductRepository,
                            @Autowired WFInstanceRepository wfInstanceRepository) {
        // Create a users
        createUsers(userRepository);
        createProducts(productRepository);
        // create instances and tasks and wfProducts
        createWorkflowData(productRepository,wfTaskRepository,wfProductRepository,wfInstanceRepository);
        
    }
    
    @AfterEach
    public void tearDown() {
    	wfProductRepository.deleteAllInBatch();
    	wfTaskRepository.deleteAllInBatch();
    	wfInstanceRepository.deleteAllInBatch();
    	orderDetailsRepository.deleteAllInBatch();
    	orderRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }
    
	private static void createUsers(UserRepository userRepository) {
	    testUser = createAndSaveUser(userRepository, "John", "Doe", RoleEnum.CUSTOMER.getCode());
	    testSuperAdmin1 = createAndSaveUser(userRepository, "John", "Doe", RoleEnum.SUPER_ADMIN.getCode());
	    testSuperAdmin2 = createAndSaveUser(userRepository, "John", "Doe", RoleEnum.SUPER_ADMIN.getCode());
	    testAdmin = createAndSaveUser(userRepository, "John", "Doe", RoleEnum.ADMIN.getCode());
	    mytestAdmin = createAndSaveUser(userRepository, "Jannet", "Doe", RoleEnum.ADMIN.getCode());

	}

	private static User createAndSaveUser(UserRepository userRepository, String firstName, String lastName, Long roleId) {
	    User user = new User(
	        firstName,              // firstName
	        lastName,               // lastName
	        roleId,                 // roleId
	        "password123",          // password
	        "john.doe@example.com", // email
	        "123 Main St",          // address
	        "555-1234",             // phone
	        "American",             // nationality
	        "Male",                 // gender
	        new Date(),             // registrationDate
	        new Date()              // birthday
	    );
	    return userRepository.save(user);
	}
	private static void createProducts(ProductRepository productRepository) {
		// Create two valid products
        testProduct1 = new Product(
        	    null,             // id will be generated by the database
        	    "Premium Blouse", // productName
        	    29.99,           // price
        	    "Red",           // color
        	    100,             // stockQuantity
        	    "Premium Material.", // description
        	    new Date()
        	);
        testProduct2 = new Product(
        	    null,             // id 
        	    "Premium suit", // productName
        	    29.99,           // price
        	    "Black",           // color
        	    70,             // stockQuantity
        	    "Premium Material.", // description
        	    new Date()
        	);     
        testProduct3 = new Product(
        	    null,             // id will be generated by the database
        	    "Premium shirt", // productName
        	    29.99,           // price
        	    "Green",           // color
        	    50,             // stockQuantity
        	    "Premium Material.", // description
        	    new Date()
        	);
        testProduct1 = productRepository.save(testProduct1);
        testProduct2 = productRepository.save(testProduct2);
        testProduct3 = productRepository.save(testProduct3);
	}
    
	private static void createWorkflowData(ProductRepository productRepository, WFTaskRepository wfTaskRepository, WFProductRepository wfProductRepository, WFInstanceRepository wfInstanceRepository) {
        // Create and save WFInstances
        WFInstance instance1 = new WFInstance();
        instance1.setProcessId(1L);
        instance1.setRequesterId(testAdmin.getId());
        instance1.setRequestDate(new Date());
        instance1.setStatus(WFInstanceStatusEnum.RUNNING.getCode());
        wfInstanceRepository.save(instance1);

        WFInstance instance2 = new WFInstance();
        instance2.setProcessId(2L);
        instance2.setRequesterId(testAdmin.getId());
        instance2.setRequestDate(new Date());
        instance2.setStatus(WFInstanceStatusEnum.RUNNING.getCode());
        wfInstanceRepository.save(instance2);

        // Create and save Products
        Product productA = new Product(4L,"Product A", 10.99, "Red", 100, "Description for Product A", new Date());
        productA = productRepository.save(productA);

        Product productB = new Product(5L,"Product B", 20.5, "Blue", 50, "Description for Product B",  new Date());
        productB = productRepository.save(productB);

        // Link Products with WF Instances
        WFProduct wfProduct1 = new WFProduct(productA, instance1.getId(), WFProductStatusEnum.ADDED.getCode());
        wfProductRepository.save(wfProduct1);

        WFProduct wfProduct2 = new WFProduct(productB, instance2.getId(), WFProductStatusEnum.ADDED.getCode());
        wfProductRepository.save(wfProduct2);


        // Create and save WFTasks
        task1 = new WFTask(instance1.getId(), testSuperAdmin1.getId(), WFAssigneeRoleEnum.SUPERADMIN.getRole(), new Date());
        task1 = wfTaskRepository.save(task1);

        task2 = new WFTask(instance2.getId(), testSuperAdmin2.getId(), WFAssigneeRoleEnum.SUPERADMIN.getRole(), new Date());
        task2 = wfTaskRepository.save(task2);
    }
	
    @Test
    public void saveProductAsUnderApproval_validAdminId() {
    	long productCountBefore = productRepository.count();
        productService.save(dummyProduct(), mytestAdmin.getId());
        // get instance using taskid
    	List<WFInstance> wfInstances = wfInstanceRepository.findByRequesterId(mytestAdmin.getId());
    	// assert that it exists
    	assertTrue(wfInstances.size()==1);
    	WFInstance wfInstance = wfInstances.get(0);
    	// check that status is updated to running
    	assertEquals((Integer)WFInstanceStatusEnum.RUNNING.getCode(),wfInstance.getStatus());
    	// get wfproduct using instance
    	WFProduct wfProduct = wfProductRepository.findByWfInstanceId(wfInstance.getId()).orElse(null);
    	assertTrue(wfProduct!=null); // checks that there exists a wfProduct with the same instanceId
    	assertEquals(wfProduct.getStatus(),WFProductStatusEnum.ADDED.getCode());
    	// check number of products before and after
    	long productCountAfter = productRepository.count();
    	assertEquals(productCountAfter,productCountBefore);
    	// check equal productId 
    	assertEquals(wfProduct.getProductId(), null);
    	// check equal productId 
    	assertEquals(wfProduct.getWfInstanceId(), wfInstance.getId());
    	// check that task is updated to show action id of underapproval
    	task1 = wfTaskRepository.findById(task1.getId()).orElse(null);
    	assertTrue(task1!=null);
    	assertEquals(null,task1.getActionId());
    	User assignee = userRepository.findById(task1.getAssigneeId()).orElse(null);
        assertTrue(assignee != null);
        assertTrue(assignee.getRoleId()==RoleEnum.SUPER_ADMIN.getCode());
    }

    @Test
    public void saveProduct_ValidSuperAdminId() {
    	long productCountBefore = productRepository.count();
    	productService.save(dummyProduct(), testSuperAdmin1.getId());
        // get last added product and check status
    	// check number of products before and after
    	long productCountAfter = productRepository.count();
    	assertEquals(productCountAfter,productCountBefore+1);
    }
    
    
    @Test
    public void getAllTasks() {
    	// get tasks
    	List<WFTaskDetails> tasks = wfProductService.getTasksByAssigneeId(testSuperAdmin1.getId());
    	assertTrue(tasks.size()>0);
    	// check all returned products if WfStatus is 1
    	for(WFTaskDetails task:tasks) {
            assertEquals(testSuperAdmin1.getId(),task.getAssigneeId());
            assertEquals(task.getAction(),null);
            assertEquals(WFAssigneeRoleEnum.SUPERADMIN.getRole(),task.getAssigneeRole());
    	}
    }
    

    @Test
    public void approveValidSuperAdmin() {
    	long productCountBefore = productRepository.count();
    	wfProductService.respondToTask(task1.getId(), testSuperAdmin1.getId(),"approve","Note",null);
    	// get instance using taskid
    	WFInstance wfInstance = wfInstanceRepository.findById(task1.getInstanceId()).orElse(null);
    	// check that status is updated to done
    	assertEquals((Integer)WFInstanceStatusEnum.DONE.getCode(),wfInstance.getStatus());
    	// get wfproduct using instance
    	WFProduct wfProduct = wfProductRepository.findByWfInstanceId(wfInstance.getId()).orElse(null);
    	// get product using wfproduct
    	Product product = productRepository.findById(wfProduct.getProductId()).orElse(null);
    	assertTrue(product!=null);
    	// check number of products before and after
    	long productCountAfter = productRepository.count();
    	assertEquals(productCountAfter,productCountBefore+1);
    	// check that task is updated to show action id of approve
    	task1 = wfTaskRepository.findById(task1.getId()).orElse(null);
    	assertEquals(WFActionEnum.APPROVED.getAction(),task1.getActionId());
    }

    @Test
    public void approveAlreadyApprovedTaskAsSuperAdmin() {
    	wfProductService.respondToTask(task1.getId(), testSuperAdmin1.getId(),"approve","Note",null);
    	long productCountBefore = productRepository.count();
    	assertThrows(BusinessException.class, () -> {
        	wfProductService.respondToTask(task1.getId(), testSuperAdmin1.getId(),"approve","Note",null);
    	});
    	long productCountAfter = productRepository.count();
    	assertEquals(productCountAfter,productCountBefore);
    	
    }

    @Test
    public void approveAlreadyRejectedTaskAsSuperAdmin() {
    	long productCountBefore = productRepository.count();
    	wfProductService.respondToTask(task1.getId(), testSuperAdmin1.getId(),"reject","Note","rejection reason");
    	assertThrows(BusinessException.class, () -> {
        	wfProductService.respondToTask(task1.getId(), testSuperAdmin1.getId(),"approve","Note",null);
    	});
    	long productCountAfter = productRepository.count();
    	assertEquals(productCountAfter,productCountBefore);
    }
    
    @Test
    public void rejectAlreadyApprovedTaskAsSuperAdmin() {
    	wfProductService.respondToTask(task1.getId(), testSuperAdmin1.getId(),"approve","Note",null);
    	long productCountBefore = productRepository.count();
    	assertThrows(BusinessException.class, () -> {
        	wfProductService.respondToTask(task1.getId(), testSuperAdmin1.getId(),"reject","Note","rejection reason");
    	});
    	long productCountAfter = productRepository.count();
    	assertEquals(productCountAfter,productCountBefore);
    }

    @Test
    public void rejectAlreadyRejectedTaskAsSuperAdmin() {
    	long productCountBefore = productRepository.count();
    	wfProductService.respondToTask(task1.getId(), testSuperAdmin1.getId(),"reject","Note","rejection reason");
    	assertThrows(BusinessException.class, () -> {
        	wfProductService.respondToTask(task1.getId(), testSuperAdmin1.getId(),"reject","Note","rejection reason");
    	});
    	long productCountAfter = productRepository.count();
    	assertEquals(productCountAfter,productCountBefore);
    }
    
    
    @Test
    public void approveWithNoTaskId() {
    	long productCountBefore = productRepository.count();
    	assertThrows(BusinessException.class, () -> {
        	wfProductService.respondToTask(null, testSuperAdmin2.getId(),"approve","Note",null);
    	});
    	long productCountAfter = productRepository.count();
    	assertEquals(productCountAfter,productCountBefore);
    }    
    @Test
    public void approveWithNoLoginId() {
    	long productCountBefore = productRepository.count();
    	assertThrows(BusinessException.class, () -> {
        	wfProductService.respondToTask(task1.getId(),null,"approve","Note",null);
    	});
    	long productCountAfter = productRepository.count();
    	assertEquals(productCountAfter,productCountBefore);
    }
    
    @Test
    public void rejectWithNoTaskId() {
    	long productCountBefore = productRepository.count();
    	assertThrows(BusinessException.class, () -> {
        	wfProductService.respondToTask(null, testSuperAdmin2.getId(),"reject","Note","rejected");
    	});
    	long productCountAfter = productRepository.count();
    	assertEquals(productCountAfter,productCountBefore);
    }
    
    @Test
    public void rejectWithNoLoginId() {
    	long productCountBefore = productRepository.count();
    	assertThrows(BusinessException.class, () -> {
        	wfProductService.respondToTask(task1.getId(), null,"reject","Note","rejected");
    	});
    	long productCountAfter = productRepository.count();
    	assertEquals(productCountAfter,productCountBefore);
    }
    
    @Test
    public void approveAsNonExistentUser() {
    	long productCountBefore = productRepository.count();
       	assertThrows(BusinessException.class, () -> {
        	wfProductService.respondToTask(task1.getId(), 150L,"approve","Note",null);
    	});	
    	long productCountAfter = productRepository.count();
    	assertEquals(productCountAfter,productCountBefore);
    }

    @Test
    public void rejectAsNonExistentUser() {
    	long productCountBefore = productRepository.count();
       	assertThrows(BusinessException.class, () -> {
        	wfProductService.respondToTask(task1.getId(), 150L,"reject","Note","rejected");
    	});
    	long productCountAfter = productRepository.count();
    	assertEquals(productCountAfter,productCountBefore);
    }
    
    @Test
    public void approveInvalidSuperAdmin() {
    	long productCountBefore = productRepository.count();
       	assertThrows(BusinessException.class, () -> {
        	wfProductService.respondToTask(task1.getId(), testSuperAdmin2.getId(),"approve","Note",null);
    	});
    	long productCountAfter = productRepository.count();
    	assertEquals(productCountAfter,productCountBefore);
    }
    
    @Test
    public void rejectInvalidSuperAdmin() {
    	long productCountBefore = productRepository.count();
       	assertThrows(BusinessException.class, () -> {
        	wfProductService.respondToTask(task1.getId(), testSuperAdmin2.getId(),"reject","rejection note","Note");
    	});	
       	long productCountAfter = productRepository.count();
    	assertEquals(productCountAfter,productCountBefore);
    }
    
    @Test
    public void rejectValidSuperAdmin() {
    	long productCountBefore = productRepository.count();

    	// Assume the rejection method exists in your service
        wfProductService.respondToTask(task1.getId(), testSuperAdmin1.getId(),"reject","note","Rejection reason");

        // Get instance using taskId
        WFInstance wfInstance = wfInstanceRepository.findById(task1.getInstanceId()).orElse(null);
        
        // Check that status is updated to rejected
        assertEquals((Integer) WFInstanceStatusEnum.DONE.getCode(), wfInstance.getStatus());
        
        // Get WFProduct using instance
        WFProduct wfProduct = wfProductRepository.findByWfInstanceId(wfInstance.getId()).orElse(null);
        
        // Get product using WFProduct
        Product product = productRepository.findById(wfProduct.getProductId()).orElse(null);
        
        assertTrue(product!=null);
        // Check that task is updated to show action id of reject
        task1 = wfTaskRepository.findById(task1.getId()).orElse(null);
        assertEquals(WFActionEnum.REJECTED.getAction(), task1.getActionId());
        
        // Check that notes and rejection reason have been updated with the rejection reason
        assertEquals("Rejection reason", task1.getRefuseReasons());
        
        // Check that notes and rejection reason have been updated with the rejection reason
        assertEquals("note", task1.getNotes());
       	long productCountAfter = productRepository.count();
    	assertEquals(productCountAfter,productCountBefore);
    }
    
    @Test
    public void approveInvalidAdmin() {
    	long productCountBefore = productRepository.count();

    	assertThrows(BusinessException.class, () -> {
        	wfProductService.respondToTask(task1.getId(), testAdmin.getId(),"approve","Note",null);
    	});
    	long productCountAfter = productRepository.count();
    	assertEquals(productCountAfter,productCountBefore);
    }
    @Test
    public void approveInvalidCustomer() {
    	long productCountBefore = productRepository.count();
    	assertThrows(BusinessException.class, () -> {
        	wfProductService.respondToTask(task1.getId(), testUser.getId(),"approve","Note",null);
    	});
    	long productCountAfter = productRepository.count();
    	assertEquals(productCountAfter,productCountBefore);
    }
    
    @Test
    public void rejectInvalidAdmin() {
    	long productCountBefore = productRepository.count();
    	assertThrows(BusinessException.class, () -> {
        	wfProductService.respondToTask(task1.getId(), testAdmin.getId(),"reject","Rejection reason","Note");
    	});
    	long productCountAfter = productRepository.count();
    	assertEquals(productCountAfter,productCountBefore);
    }
    @Test
    public void rejectInvalidCustomer() {
    	long productCountBefore = productRepository.count();
    	assertThrows(BusinessException.class, () -> {
        	wfProductService.respondToTask(task1.getId(), testUser.getId(),"reject","Rejection reason","Note");
    	});
    	long productCountAfter = productRepository.count();
    	assertEquals(productCountAfter,productCountBefore);
    }
    
    private Product dummyProduct() {
        return Product.builder().productName("Laptop").price(1200.00).color("Silver").stockQuantity(50)
                .description("High-performance laptop").build();
    }

}
