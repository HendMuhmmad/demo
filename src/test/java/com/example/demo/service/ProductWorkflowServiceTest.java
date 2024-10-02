package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.enums.RoleEnum;
import com.example.demo.enums.workflow.WFActionEnum;
import com.example.demo.enums.workflow.WFAsigneeRoleEnum;
import com.example.demo.enums.workflow.WFInstanceStatusEnum;
import com.example.demo.enums.workflow.WFStatusEnum;
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

@SpringBootTest
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
	private static User testSuperAdmin1;
	private static User testSuperAdmin2;
	private static User testHeadOfDepartment;
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
	    testUser = createAndSaveUser(userRepository, "John", "Doe", 4L);
	    testHeadOfDepartment = createAndSaveUser(userRepository, "John", "Doe", 1L);
	    testSuperAdmin1 = createAndSaveUser(userRepository, "John", "Doe", 2L);
	    testSuperAdmin2 = createAndSaveUser(userRepository, "John", "Doe", 2L);
	    testAdmin = createAndSaveUser(userRepository, "John", "Doe", 3L);
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
        	    (long)0, new Date()
        	);
        testProduct2 = new Product(
        	    null,             // id 
        	    "Premium suit", // productName
        	    29.99,           // price
        	    "Black",           // color
        	    70,             // stockQuantity
        	    "Premium Material.", // description
        	    (long)1, new Date()
        	);     
        testProduct3 = new Product(
        	    null,             // id will be generated by the database
        	    "Premium shirt", // productName
        	    29.99,           // price
        	    "Green",           // color
        	    50,             // stockQuantity
        	    "Premium Material.", // description
        	    (long)2, new Date()
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
        Product productA = new Product(4L,"Product A", 10.99, "Red", 100, "Description for Product A", WFStatusEnum.UNDERAPPROVAL.getCode(), new Date());
        productA = productRepository.save(productA);

        Product productB = new Product(5L,"Product B", 20.5, "Blue", 50, "Description for Product B", WFStatusEnum.UNDERAPPROVAL.getCode(), new Date());
        productB = productRepository.save(productB);

        // Link Products with WF Instances
        WFProduct wfProduct1 = new WFProduct(productA.getId(), instance1.getId());
        wfProductRepository.save(wfProduct1);

        WFProduct wfProduct2 = new WFProduct(productB.getId(), instance2.getId());
        wfProductRepository.save(wfProduct2);

        // Create and save WFTasks
        task1 = new WFTask(instance1.getId(), testSuperAdmin1.getId(), WFAsigneeRoleEnum.SUPERADMIN.getRole(), new Date());
        task1 = wfTaskRepository.save(task1);

        task2 = new WFTask(instance2.getId(), testSuperAdmin2.getId(), WFAsigneeRoleEnum.SUPERADMIN.getRole(), new Date());
        task2 = wfTaskRepository.save(task2);
    }
	
    @Test
    public void saveProductAsUnderApproval_validAdminId() {
        productService.save(dummyProduct(), testAdmin.getId());
        // get instance using taskid
    	WFInstance wfInstance = wfInstanceRepository.findById(task1.getInstanceId()).orElse(null);
    	// assert that it exists
    	assertTrue(wfInstance!=null);
    	// check that status is updated to running
    	assertEquals((Integer)WFInstanceStatusEnum.RUNNING.getCode(),wfInstance.getStatus());
    	// get wfproduct using instance
    	WFProduct wfProduct = wfProductRepository.findByWfInstanceId(wfInstance.getId()).orElse(null);
    	assertTrue(wfProduct!=null); // checks that there exists a wfProduct with the same instanceId
    	// get product using product wf
    	Product product = productRepository.findById(wfProduct.getProductId()).orElse(null);
        assertTrue(product!=null);
        // assert product under approval
        assertEquals(WFStatusEnum.UNDERAPPROVAL.getCode(),product.getWfStatus());
    	// check equal productId 
    	assertEquals(wfProduct.getProductId(), product.getId());
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
        productService.save(dummyProduct(), testSuperAdmin1.getId());
        // get last added product and check status
        Product product = productRepository.findTopByOrderByIdDesc();
        assertTrue(product!=null);
        assertEquals(Long.valueOf(1),product.getWfStatus());
    }
    
    @Test
    public void getAllProducts() {
    	// get products
    	List<Product> products = productService.getAllProduct();
    	// check all returned products if WfStatus is 1
    	for(Product product:products) {
            assertEquals(Long.valueOf(1),product.getWfStatus());
    	}
    }
    
    @Test
    public void getAllTasks() {
    	// get tasks
    	List<WFTaskDetails> tasks = wfProductService.getTasksByAssigneeId(testSuperAdmin1.getId());
    	assertTrue(tasks.size()>0);
    	// check all returned products if WfStatus is 1
    	for(WFTaskDetails task:tasks) {
            assertEquals(WFStatusEnum.UNDERAPPROVAL.getCode(),task.getWfStatus());
            assertEquals(testSuperAdmin1.getId(),task.getAssigneeId());
            assertEquals(WFAsigneeRoleEnum.SUPERADMIN.getRole(),task.getAssigneeRole());
    	}
    }
    

    @Test
    public void approveValidSuperAdmin() {
    	wfProductService.approveTask(task1.getId(), testSuperAdmin1.getId(),"Note");
    	// get instance using taskid
    	WFInstance wfInstance = wfInstanceRepository.findById(task1.getInstanceId()).orElse(null);
    	// check that status is updated to done
    	assertEquals((Integer)WFInstanceStatusEnum.DONE.getCode(),wfInstance.getStatus());
    	// TODO: check that last action's instance is updated 
    	// get wfproduct using instance
    	WFProduct wfProduct = wfProductRepository.findByWfInstanceId(wfInstance.getId()).orElse(null);
    	// get product using wfproduct
    	Product product = productRepository.findById(wfProduct.getProductId()).orElse(null);
    	// check for product Wfstatus
    	assertEquals(WFStatusEnum.APPROVED.getCode(), product.getWfStatus());
    	// check that task is updated to show action id of approve
    	task1 = wfTaskRepository.findById(task1.getId()).orElse(null);
    	assertEquals(WFActionEnum.APPROVED.getAction(),task1.getActionId());
    }

    @Test
    public void approveAlreadyApprovedTaskAsSuperAdmin() {
    	wfProductService.approveTask(task1.getId(), testSuperAdmin1.getId(),"Note");
    	assertThrows(BusinessException.class, () -> {
        	wfProductService.approveTask(task1.getId(), testSuperAdmin2.getId(),"Note");
    	});
    }

    @Test
    public void approveAlreadyRejectedTaskAsSuperAdmin() {
    	wfProductService.rejectTask(task1.getId(), testSuperAdmin1.getId(),"rejection reason","Note");
    	assertThrows(BusinessException.class, () -> {
        	wfProductService.approveTask(task1.getId(), testSuperAdmin2.getId(),"Note");
    	});
    }
    
    @Test
    public void rejectAlreadyApprovedTaskAsSuperAdmin() {
    	wfProductService.approveTask(task1.getId(), testSuperAdmin1.getId(),"Note");
    	assertThrows(BusinessException.class, () -> {
        	wfProductService.rejectTask(task1.getId(), testSuperAdmin1.getId(),"rejection reason","Note");
    	});
    }

    @Test
    public void rejectAlreadyRejectedTaskAsSuperAdmin() {
    	wfProductService.rejectTask(task1.getId(), testSuperAdmin1.getId(),"rejection reason","Note");
    	assertThrows(BusinessException.class, () -> {
        	wfProductService.rejectTask(task1.getId(), testSuperAdmin1.getId(),"rejection reason","Note");
    	});
    }
    
    
    @Test
    public void approveWithNoTaskId() {
    	assertThrows(BusinessException.class, () -> {
        	wfProductService.approveTask(null, testSuperAdmin2.getId(),"Note");
    	});
    }    
    @Test
    public void approveWithNoLoginId() {
    	assertThrows(BusinessException.class, () -> {
        	wfProductService.approveTask(task1.getId(), null,"Note");
    	});
    }
    
    @Test
    public void rejectWithNoTaskId() {
    	assertThrows(BusinessException.class, () -> {
        	wfProductService.rejectTask(null, testSuperAdmin1.getId(),"rejection reason","Note");
    	});
    }
    
    @Test
    public void rejectWithNoLoginId() {
    	assertThrows(BusinessException.class, () -> {
        	wfProductService.rejectTask(task1.getId(), null,"rejection reason","Note");
    	});
    }
    
    @Test
    public void approveAsNonExistentUser() {
       	assertThrows(BusinessException.class, () -> {
        	wfProductService.approveTask(task1.getId(), 150L,"Note");
    	});	
    }

    @Test
    public void rejectAsNonExistentUser() {
       	assertThrows(BusinessException.class, () -> {
        	wfProductService.rejectTask(task1.getId(), 150L,null,"Note");
    	});	
    }
    
    @Test
    public void approveInvalidSuperAdmin() {
       	assertThrows(BusinessException.class, () -> {
        	wfProductService.approveTask(task1.getId(), testSuperAdmin2.getId(),"Note");
    	});	
    }
    
    @Test
    public void rejectInvalidSuperAdmin() {
       	assertThrows(BusinessException.class, () -> {
        	wfProductService.rejectTask(task1.getId(), testSuperAdmin2.getId(),"rejection note","Note");
    	});	
    }
    
    @Test
    public void rejectValidSuperAdmin() {
        // Assume the rejection method exists in your service
        wfProductService.rejectTask(task1.getId(), testSuperAdmin1.getId(),"Rejection reason","note");

        // Get instance using taskId
        WFInstance wfInstance = wfInstanceRepository.findById(task1.getInstanceId()).orElse(null);
        
        // Check that status is updated to rejected
        assertEquals((Integer) WFInstanceStatusEnum.DONE.getCode(), wfInstance.getStatus());
        
        // Get WFProduct using instance
        WFProduct wfProduct = wfProductRepository.findByWfInstanceId(wfInstance.getId()).orElse(null);
        
        // Get product using WFProduct
        Product product = productRepository.findById(wfProduct.getProductId()).orElse(null);
        
        // Check for product WFStatus
        assertEquals(WFStatusEnum.REJECTED.getCode(), product.getWfStatus());
        
        // Check that task is updated to show action id of reject
        task1 = wfTaskRepository.findById(task1.getId()).orElse(null);
        assertEquals(WFActionEnum.REJECTED.getAction(), task1.getActionId());
        
        // Check that notes and rejection reason have been updated with the rejection reason
        assertEquals("Rejection reason", task1.getRefuseReasons());
        
        // Check that notes and rejection reason have been updated with the rejection reason
        assertEquals("note", task1.getNotes());

    }
    
    @Test
    public void approveInvalidAdmin() {
    	assertThrows(BusinessException.class, () -> {
        	wfProductService.approveTask(task1.getId(), testAdmin.getId(),"Note");
    	});
    }
    @Test
    public void approveInvalidCustomer() {
    	assertThrows(BusinessException.class, () -> {
        	wfProductService.approveTask(task1.getId(), testUser.getId(),"Note");
    	});
    }
    
    @Test
    public void rejectInvalidAdmin() {
    	assertThrows(BusinessException.class, () -> {
        	wfProductService.rejectTask(task1.getId(), testAdmin.getId(),"Rejection reason","Note");
    	});
    }
    @Test
    public void rejectInvalidCustomer() {
    	assertThrows(BusinessException.class, () -> {
        	wfProductService.rejectTask(task1.getId(), testUser.getId(),"Rejection reason","Note");
    	});
    }
    
    // TODO: add failing cases 

    private Product dummyProduct() {
        return Product.builder().id(1L).productName("Laptop").price(1200.00).color("Silver").stockQuantity(50)
                .description("High-performance laptop").build();
    }

    private Optional<Product> dummyOptionalProduct() {
        return Optional.of(dummyProduct());
    }
}
