package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.enums.workflow.WFAsigneeRoleEnum;
import com.example.demo.enums.workflow.WFInstanceStatusEnum;
import com.example.demo.enums.workflow.WFStatusEnum;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.orm.Order;
import com.example.demo.model.orm.OrderDetails;
import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.User;
import com.example.demo.model.orm.workflow.WFInstance;
import com.example.demo.model.orm.workflow.WFProduct;
import com.example.demo.model.orm.workflow.WFTask;
import com.example.demo.repository.OrderDetailsRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.workflow.WFInstanceRepository;
import com.example.demo.repository.workflow.WFProductRepository;
import com.example.demo.repository.workflow.WFTaskDetailsRepository;
import com.example.demo.repository.workflow.WFTaskRepository;

@SpringBootTest
public class OrderWorkflowServiceTest {
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
    public OrderService orderService;
	
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
    	orderRepository.deleteAllInBatch();;
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
        	    WFStatusEnum.UNDERAPPROVAL.getCode(), new Date()
        	);
        testProduct2 = new Product(
        	    null,             // id 
        	    "Premium suit", // productName
        	    29.99,           // price
        	    "Black",           // color
        	    70,             // stockQuantity
        	    "Premium Material.", // description
        	    WFStatusEnum.APPROVED.getCode(), new Date()
        	);     
        testProduct3 = new Product(
        	    null,             // id will be generated by the database
        	    "Premium shirt", // productName
        	    29.99,           // price
        	    "Green",           // color
        	    50,             // stockQuantity
        	    "Premium Material.", // description
        	    WFStatusEnum.REJECTED.getCode(), new Date()
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
    public void testValidOrder() {
    	List <OrderDetails> orderDetails = new ArrayList<OrderDetails>();
    	orderDetails.add(new OrderDetails(null,null,testProduct2.getId(),2));
    	Order resultOrder = orderService.createOrder(testUser.getId(), orderDetails);

		// Assert
		assertNotNull(resultOrder);
		assertEquals(testUser.getId(), resultOrder.getUserId());
		assertTrue(resultOrder.getTotalPrice() == testProduct1.getPrice()*2);
    }
    
    @Test
    public void testInvalidOrderUsingUnderApprovalProduct() {
    	List <OrderDetails> orderDetails = new ArrayList<OrderDetails>();
    	orderDetails.add(new OrderDetails(null,null,testProduct1.getId(),2));

		// Assert
    	assertThrows(BusinessException.class, () -> {
			orderService.createOrder(testUser.getId(), orderDetails);
		});
    }
    
    @Test
    public void testInvalidOrderUsingRejectedProduct() {
    	List <OrderDetails> orderDetails = new ArrayList<OrderDetails>();
    	orderDetails.add(new OrderDetails(null,null,testProduct3.getId(),2));
		// Assert
       	assertThrows(BusinessException.class, () -> {
    			orderService.createOrder(testUser.getId(), orderDetails);
    	});
    }
    

    private Product dummyProduct() {
        return Product.builder().id(1L).productName("Laptop").price(1200.00).color("Silver").stockQuantity(50)
                .description("High-performance laptop").build();
    }

    private Optional<Product> dummyOptionalProduct() {
        return Optional.of(dummyProduct());
    }
}
