package com.example.demo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.enums.workflow.WFAssigneeRoleEnum;
import com.example.demo.enums.workflow.WFInstanceStatusEnum;
import com.example.demo.enums.workflow.WFProductStatusEnum;
import com.example.demo.enums.workflow.WFStatusEnum;
import com.example.demo.model.dto.ProductDto;
import com.example.demo.model.dto.workflow.ResponseDto;
import com.example.demo.model.dto.workflow.ResponseDto;
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
import com.example.demo.service.ProductService;
import com.example.demo.service.UserService;
import com.example.demo.service.WFProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ProductWorkflowControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

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

	private static User testAdmin;
	private static Long testAdminId;
	private static User testSuperAdmin1;
	private static Long testSuperAdmin1Id;
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

	    testSuperAdmin1 = createAndSaveUser(userRepository, "John", "Doe", 2L);
	    testSuperAdmin2 = createAndSaveUser(userRepository, "John", "Doe", 2L);
	    testSuperAdmin1Id = testSuperAdmin1.getId();
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

        Product productB = new Product(5L,"Product B", 20.5, "Blue", 50, "Description for Product B", new Date());
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
	public void saveProductAsAdmin() throws Exception {
		ProductDto product = new ProductDto(20, 100.0, "Product 1", "Red", "Description 1", 20, 0L, null, testAdminId);
		mockMvc.perform(post("/api/product/createProduct").contentType("application/json")
				.content(objectMapper.writeValueAsString(product))).andExpect(status().isOk());
	}
	@Test
	public void saveProductAsSuperAdmin() throws Exception {
		ProductDto product = new ProductDto(20, 100.0, "Product 1", "Red", "Description 1", 20, 0L, null, testSuperAdmin1Id);
		mockMvc.perform(post("/api/product/createProduct").contentType("application/json")
				.content(objectMapper.writeValueAsString(product))).andExpect(status().isOk());
	}
	
	@Test
	public void approveProductAsSuperAdmin() throws Exception {
		ResponseDto responseDto = new ResponseDto();
		responseDto.setLoginId(testSuperAdmin1Id);
		responseDto.setTaskId(task1.getId());
		responseDto.setResponse("Approve");
		mockMvc.perform(post("/api/product/tasks/respondToRequest").contentType("application/json")
				.content(objectMapper.writeValueAsString(responseDto))).andExpect(status().isOk());
	}
	@Test
	public void approveProductAsInvalidAdmin() throws Exception {
		ResponseDto responseDto = new ResponseDto();
		responseDto.setLoginId(testAdminId);
		responseDto.setTaskId(task1.getId());
		responseDto.setResponse("Approve");
		mockMvc.perform(post("/api/product/tasks/respondToRequest").contentType("application/json")
				.content(objectMapper.writeValueAsString(responseDto))).andExpect(status().isBadRequest());
	}
	
	@Test
	public void rejectProductAsSuperAdmin() throws Exception {
		ResponseDto responseDto = new ResponseDto();
		responseDto.setLoginId(testSuperAdmin1Id);
		responseDto.setTaskId(task1.getId());
		responseDto.setResponse("Reject");
		mockMvc.perform(post("/api/product/tasks/respondToRequest").contentType("application/json")
				.content(objectMapper.writeValueAsString(responseDto))).andExpect(status().isOk());
	}
	@Test
	public void rejectProductAsInvalidAdmin() throws Exception {
		ResponseDto responseDto = new ResponseDto();
		responseDto.setLoginId(testAdminId);
		responseDto.setTaskId(task1.getId());
		responseDto.setResponse("Reject");
		mockMvc.perform(post("/api/product/tasks/respondToRequest").contentType("application/json")
				.content(objectMapper.writeValueAsString(responseDto))).andExpect(status().isBadRequest());
	}
	
	}
