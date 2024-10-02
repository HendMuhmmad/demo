package com.example.demo.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.enums.workflow.WFAsigneeRoleEnum;
import com.example.demo.enums.workflow.WFInstanceStatusEnum;
import com.example.demo.enums.workflow.WFStatusEnum;
import com.example.demo.model.dto.orderDetails.OrderDetailsCreationDTO;
import com.example.demo.model.orm.OrderDetails;
import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.Order;
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
import com.example.demo.service.WFProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
public class OrderWorkflowControllerTest {
	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private OrderDetailsRepository orderDetailsRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private UserRepository userRepository;
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
    
	private static User testUser;
    private static Long testUserId;
	private static User testAdmin;
	private static Long testAdminId;
	private static User testSuperAdmin1;
	private static Long testSuperAdmin1Id;
	private static User testSuperAdmin2;
	private static Long testSuperAdmin2Id;
	private static User testHeadOfDepartment;
	private static Long testHeadOfDepartmentId;
    private static Product testProduct1;
    private static Product testProduct2;
    private static Product testProduct3;    
    private static Product testProduct4;    
    private static WFTask task1;
    private static WFTask task2;
    private static String orderNum = "TEST-ORD-01";
	
	@BeforeAll
    public static void init(@Autowired UserRepository userRepository, 
                            @Autowired ProductRepository productRepository,
                            @Autowired OrderRepository orderRepository, 
                            @Autowired OrderDetailsRepository orderDetailsRepository,
                            @Autowired WFTaskRepository wfTaskRepository,
                            @Autowired WFProductRepository wfProductRepository,
                            @Autowired WFInstanceRepository wfInstanceRepository) {
        // Create a users
		

        createUsers(userRepository);
		
        createProducts(productRepository);
        
        createOrderForTestUser(orderRepository, orderDetailsRepository);
        // create instances and tasks and wfProducts
        createWorkflowData(productRepository,wfTaskRepository,wfProductRepository,wfInstanceRepository);

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
	private static void createProducts(ProductRepository productRepository) {
		// Create two valid products
        testProduct1 = new Product(
        	    null,             // id will be generated by the database
        	    "Premium Blouse", // productName
        	    29.99,           // price
        	    "Red",           // color
        	    100,             // stockQuantity
        	    "Premium Material.", // description
        	    WFStatusEnum.UNDERAPPROVAL.getCode(),
        	    new Date()
        	);
        testProduct2 = new Product(
        	    null,             // id 
        	    "Premium suit", // productName
        	    29.99,           // price
        	    "Black",           // color
        	    70,             // stockQuantity
        	    "Premium Material.", // description
        	    WFStatusEnum.APPROVED.getCode(),
        	    new Date()
        	);   
        
        testProduct3 = new Product(
        	    null,             // id will be generated by the database
        	    "Premium shirt", // productName
        	    29.99,           // price
        	    "Green",           // color
        	    50,             // stockQuantity
        	    "Premium Material.", // description
        	    WFStatusEnum.REJECTED.getCode(),
        	    new Date()
        	);
        testProduct4 = new Product(
        	    null,             // id 
        	    "Premium dress", // productName
        	    29.99,           // price
        	    "Teal",           // color
        	    90,             // stockQuantity
        	    "Premium Material.", // description
        	    WFStatusEnum.APPROVED.getCode(),
        	    new Date()
        	);  
        testProduct1 = productRepository.save(testProduct1);
        testProduct2 = productRepository.save(testProduct2);
        testProduct3 = productRepository.save(testProduct3);
        testProduct4 = productRepository.save(testProduct4);
	}

	private static void createUsers(UserRepository userRepository) {
	    testUser = createAndSaveUser(userRepository, "John", "Doe", 4L);
	    testUserId = testUser.getId();
	    testHeadOfDepartment = createAndSaveUser(userRepository, "John", "Doe", 1L);
	    testHeadOfDepartmentId = testHeadOfDepartment.getId();
	    testSuperAdmin1 = createAndSaveUser(userRepository, "John", "Doe", 2L);
	    testSuperAdmin2 = createAndSaveUser(userRepository, "John", "Doe", 2L);
	    testSuperAdmin1Id = testSuperAdmin1.getId();
	    testSuperAdmin2Id = testSuperAdmin2.getId();
	    testAdmin = createAndSaveUser(userRepository, "John", "Doe", 3L);
	    testAdminId = testAdmin.getId();

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
	
	private static void createOrderForTestUser(OrderRepository orderRepository, OrderDetailsRepository orderDetailsRepository) {
	    // Create a list of product IDs and their corresponding quantities
	    List<Long> productIds = Arrays.asList(testProduct1.getId());
	    List<Integer> quantities = Arrays.asList(1, 2);

	    // Create and save the order
	    Order order = new Order();
	    order.setUserId(testUser.getId());
	    order.setOrderNumber(orderNum);
	    order.setTransactionDate(new Date());
	    order = orderRepository.save(order);

	    // Create and save order details
	    for (int i = 0; i < productIds.size(); i++) {
	        orderDetailsRepository.save(new OrderDetails(0L,order.getId(), productIds.get(i), quantities.get(i)));
	    }
	}

    @AfterAll
    @Transactional
    public void tearDown() {
    	wfProductRepository.deleteAllInBatch();
    	wfTaskRepository.deleteAllInBatch();
    	wfInstanceRepository.deleteAllInBatch();
      	orderDetailsRepository.deleteAllInBatch();
        orderRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        
    }

    @Test
    public void createValidOrderTest() {
    	Long userId = testUser.getId();
        List<OrderDetailsCreationDTO> dtoList = createOrderDetails();
        
        try {
            mockMvc.perform(post("/api/v1/orders/createOrder")
                    .param("userId", Long.toString(userId))
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(dtoList)))
                  .andExpect(status().isCreated());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	private List<OrderDetailsCreationDTO> createOrderDetails() {
		Long[] productIds = {testProduct2.getId(), testProduct4.getId()};
        int[] quantity = {2, 3};
        List<OrderDetailsCreationDTO> dtoList = new ArrayList<>();
        dtoList.add(new OrderDetailsCreationDTO(productIds[0], quantity[0]));
        dtoList.add(new OrderDetailsCreationDTO(productIds[1], quantity[1]));
		return dtoList;
	}

	private List<OrderDetailsCreationDTO> createInvalidOrderDetails() {
		Long[] productIds = {testProduct1.getId(), testProduct3.getId()};
        int[] quantity = {2, 3};
        List<OrderDetailsCreationDTO> dtoList = new ArrayList<>();
        dtoList.add(new OrderDetailsCreationDTO(productIds[0], quantity[0]));
        dtoList.add(new OrderDetailsCreationDTO(productIds[1], quantity[1]));
		return dtoList;
	}

	
	@Test
	public void createOrderWithInvalidProductsTest() {
		int userId = 5;
        List<OrderDetailsCreationDTO> dtoList = createInvalidOrderDetails();
		int ordersCount = orderRepository.findAll().size();
		try {
			
			mockMvc.perform(post("/api/v1/orders/createOrder")
					.param("userId", Integer.toString(userId))
			        .contentType("application/json")
			        .content(objectMapper.writeValueAsString(dtoList)))
			      .andExpect(status().isBadRequest());
			int updatedOrdersCount = orderRepository.findAll().size();
			assertTrue(ordersCount==updatedOrdersCount);
			
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
	}
}
