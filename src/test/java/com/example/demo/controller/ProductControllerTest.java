package com.example.demo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

import com.example.demo.enums.RoleEnum;
import com.example.demo.model.dto.ProductDto;
import com.example.demo.model.dto.ProductUpdateStockQuantityDTO;
import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.User;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.workflow.WFInstanceRepository;
import com.example.demo.repository.workflow.WFProductRepository;
import com.example.demo.repository.workflow.WFTaskDetailsRepository;
import com.example.demo.repository.workflow.WFTaskRepository;
import com.example.demo.service.WFProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ProductControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProductRepository productRepository;
	
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

	private Long adminUserId;
	private Long superAdminUserId;
	private Long headOfDepartmentUserId;
	private Product product1;
	private Product product2;
	private Product product3;

	@BeforeEach
	public void setUp() {
		// Create and save test users
		User adminUser = new User("Admin", "User", RoleEnum.ADMIN.getCode(), "adminpass", "admin@example.com",
				"Admin Address", "123456789", "Egyptian", "Male", new Date(), new Date());
		User superAdminUser = new User("Super", "Admin", RoleEnum.SUPER_ADMIN.getCode(), "superadminpass",
				"superadmin@example.com", "Super Admin Address", "987654322", "Egyptian", "Male", new Date(), new Date());
		User headOfDepartmentUser = new User("Head", "Department", RoleEnum.HEAD_OF_DEPARTMENT.getCode(), "hodpass",
				"hod@example.com", "HOD Address", "987654321", "Egyptian", "Male", new Date(), new Date());

		adminUserId = userRepository.save(adminUser).getId();
		superAdminUserId = userRepository.save(superAdminUser).getId();
		headOfDepartmentUserId = userRepository.save(headOfDepartmentUser).getId();

		// Create and save test products
		product1 = productRepository
				.save(new Product(null, "Product 1", 100.0, "Red", 20, "Description 1", null, new Date()));
		product2 = productRepository
				.save(new Product(null, "Product 2", 150.0, "Blue", 30, "Description 2", null, new Date()));
		product3 = productRepository
				.save(new Product(null, "Product 3", 200.0, "Green", 40, "Description 3", null, new Date()));
	}
	
    @AfterEach
    public void tearDown() {
    	wfProductRepository.deleteAllInBatch();
    	wfTaskRepository.deleteAllInBatch();
    	wfInstanceRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

	@Test
	public void saveProduct() throws Exception {
		ProductDto product = new ProductDto(20, 100.0, "Product 1", "Red", "Description 1", 20, 0L, null, adminUserId);
		mockMvc.perform(post("/api/product/createProduct").contentType("application/json")
				.content(objectMapper.writeValueAsString(product))).andExpect(status().isOk());
	}

	@Test
	public void saveProduct_withoutProductName() throws Exception {
		ProductDto product = new ProductDto(20, 100.0, null, "Red", "Description 1", 20, 0L, null, adminUserId);
		mockMvc.perform(post("/api/product/createProduct").contentType("application/json")
				.content(objectMapper.writeValueAsString(product))).andExpect(status().isBadRequest());
	}

	@Test
	public void saveProduct_withoutPrice() throws Exception {
		ProductDto product = new ProductDto();
		product.setProductName("product name");
		product.setColor("Red");
		product.setDescription("Description 1");
		product.setStockQuantity(20);
		product.setLoginId(adminUserId);
		mockMvc.perform(post("/api/product/createProduct").contentType("application/json")
				.content(objectMapper.writeValueAsString(product))).andExpect(status().isBadRequest());
	}

	@Test
	public void saveProduct_invalidLoginId() throws Exception {
		ProductDto product = new ProductDto(20, 100.0, null, "Red", "Description 1", 20, 0L, null,
				headOfDepartmentUserId);
		mockMvc.perform(post("/api/product/createProduct").contentType("application/json")
				.content(objectMapper.writeValueAsString(product))).andExpect(status().isBadRequest());
	}

	@Test
	public void updateProductStockQuantity() throws Exception {
		ProductUpdateStockQuantityDTO product = new ProductUpdateStockQuantityDTO(product1.getId(), 20, adminUserId);
		mockMvc.perform(put("/api/product/updateProductStockQuantity").contentType("application/json")
				.content(objectMapper.writeValueAsString(product))).andExpect(status().isOk());
	}

	@Test
	public void updateProductStockQuantity_invalidProductId() throws Exception {
		ProductUpdateStockQuantityDTO product = new ProductUpdateStockQuantityDTO(999L, 20, adminUserId);
		mockMvc.perform(put("/api/product/updateProductStockQuantity").contentType("application/json")
				.content(objectMapper.writeValueAsString(product))).andExpect(status().isBadRequest());
	}

	@Test
	public void updateProductStockQuantity_invalidLoginId() throws Exception {
		ProductUpdateStockQuantityDTO product = new ProductUpdateStockQuantityDTO(1L, 20, headOfDepartmentUserId);
		mockMvc.perform(put("/api/product/updateProductStockQuantity").contentType("application/json")
				.content(objectMapper.writeValueAsString(product))).andExpect(status().isBadRequest());
	}

	@Test
	public void deleteProduct() throws Exception {
		mockMvc.perform(
				delete("/api/product/deleteProduct/{productId}", product2.getId()).param("loginId", String.valueOf(adminUserId)))
				.andExpect(status().isOk());
	}

	@Test
	public void deleteProduct_invalidProductId() throws Exception {
		mockMvc.perform(
				delete("/api/product/deleteProduct/{productId}", 999L).param("loginId", String.valueOf(adminUserId)))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void deleteProduct_invalidLoginId() throws Exception {
		mockMvc.perform(delete("/api/product/deleteProduct/{productId}", 1L).param("loginId",
				String.valueOf(headOfDepartmentUserId))).andExpect(status().isBadRequest());
	}
}
