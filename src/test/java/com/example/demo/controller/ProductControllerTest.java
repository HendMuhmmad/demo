package com.example.demo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.enums.RoleEnum;
import com.example.demo.model.dto.ProductDto;
import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.User;
import com.example.demo.model.orm.workflow.WFActions;
import com.example.demo.model.orm.workflow.WFProcess;
import com.example.demo.model.orm.workflow.WFProcessGroup;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.workflow.WFActionsRepository;
import com.example.demo.repository.workflow.WFInstanceRepository;
import com.example.demo.repository.workflow.WFProcessGroupRepository;
import com.example.demo.repository.workflow.WFProcessRepository;
import com.example.demo.repository.workflow.WFTaskRepository;
import com.example.demo.repository.workflow.product.WFProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)

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
	private WFTaskRepository wfTaskRepository;

	@Autowired
	private WFInstanceRepository wfInstanceRepository;

	@Autowired
	private WFProductRepository wfProductRepository;

	@Autowired
	private WFProcessGroupRepository wfProcessGroupRepository;

	@Autowired
	private WFProcessRepository wfProcessRepository;

	@Autowired
	private WFActionsRepository wfActionRepository;

	private Long adminUserId;
	private Long superAdminUserId;
	private Long headOfDepartmentUserId;
	private Product product1;
	private Product product2;
	private Product product3;

	@BeforeAll
	public void setUpWorkFlow() {
		WFProcessGroup savedProcessGroup = new WFProcessGroup();
		savedProcessGroup.setProcessGroupName("Products");
		wfProcessGroupRepository.save(savedProcessGroup);

		WFProcess savedProcess = new WFProcess();
		savedProcess.setName("Add Product");
		savedProcess.setGroupId(savedProcessGroup.getId());
		wfProcessRepository.save(savedProcess);
		
		WFProcess savedProcessUpdate = new WFProcess();
		savedProcessUpdate.setName("Update Product");
		savedProcessUpdate.setGroupId(savedProcessGroup.getId());
		wfProcessRepository.save(savedProcessUpdate);

		WFProcess savedProcessDelete = new WFProcess();
		savedProcessDelete.setName("Delete Product");
		savedProcessDelete.setGroupId(savedProcessDelete.getId());
		wfProcessRepository.save(savedProcessDelete);
		
		// Insert into ECO_WF_ACTIONS
		WFActions approveAction = new WFActions();
		approveAction.setActionName("APPROVE");
		wfActionRepository.save(approveAction);

		WFActions rejectAction = new WFActions();
		rejectAction.setActionName("REJECT");
		wfActionRepository.save(rejectAction);

	}

	@BeforeEach
	public void setUp() {
		// Create and save test users
		User adminUser = new User("Admin", "User", RoleEnum.ADMIN.getCode(), "adminpass", "admin@example.com",
				"Admin Address", "123456789", "Egyptian", "Male", new Date(), new Date());
		User superAdminUser = new User("Admin", "User", RoleEnum.SUPER_ADMIN.getCode(), "adminpass",
				"admin@example.com", "Admin Address", "123456789", "Egyptian", "Male", new Date(), new Date());
		User headOfDepartmentUser = new User("Head", "Department", RoleEnum.HEAD_OF_DEPARTMENT.getCode(), "hodpass",
				"hod@example.com", "HOD Address", "987654321", "Egyptian", "Male", new Date(), new Date());

		adminUserId = userRepository.save(adminUser).getId();
		superAdminUserId = userRepository.save(superAdminUser).getId();

		headOfDepartmentUserId = userRepository.save(headOfDepartmentUser).getId();

		// Create and save test products
		product1 = productRepository
				.save(new Product(null, "Product 1", 100.0, "Red", 20, "Description 1", new Date()));
		product2 = productRepository
				.save(new Product(null, "Product 2", 150.0, "Blue", 30, "Description 2", new Date()));
		product3 = productRepository
				.save(new Product(null, "Product 3", 200.0, "Green", 40, "Description 3", new Date()));

	}

	@AfterEach
	public void tearDown() {
		wfTaskRepository.deleteAllInBatch();
		wfProductRepository.deleteAllInBatch();
		wfInstanceRepository.deleteAllInBatch();
		userRepository.deleteAllInBatch();
		productRepository.deleteAllInBatch();
	}

	@AfterAll
	public void tearWorkflowDown() {
		wfActionRepository.deleteAll();
		wfProcessRepository.deleteAll();
		wfProcessGroupRepository.deleteAll();
		wfActionRepository.resetSequence(1);
		wfProcessRepository.resetSequence(1);
		wfProcessGroupRepository.resetSequence(1);

	}

	@Test
	public void saveProduct() throws Exception {
		ProductDto product = new ProductDto(null, 20, 100.0, "name", "red", "desc", adminUserId, 0);
		mockMvc.perform(post("/api/product/createProduct").contentType("application/json")
				.content(objectMapper.writeValueAsString(product))).andExpect(status().isOk());
	}

	@Test
	public void saveProduct_withoutProductName() throws Exception {
		ProductDto product = new ProductDto(null, 20, 100.0, null, "red", "desc", adminUserId, 0);
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
		ProductDto product = new ProductDto(null, 20, 100.0, "name", "red", "desc", headOfDepartmentUserId, 0);
		mockMvc.perform(post("/api/product/createProduct").contentType("application/json")
				.content(objectMapper.writeValueAsString(product))).andExpect(status().isBadRequest());
	}

	@Test
	public void updateProduct() throws Exception {
		ProductDto productDto = new ProductDto(product1.getId(), 200, 20.3, "new name", "blue", "updated", adminUserId, 0);
		mockMvc.perform(put("/api/product/updateProduct").contentType("application/json")
				.content(objectMapper.writeValueAsString(productDto))).andExpect(status().isOk());
	}

	@Test
	public void updateProduct_invalidProductId() throws Exception {
		ProductDto productDto = new ProductDto(-1L, 200, 20.3, "new name", "blue", "updated", adminUserId, 0);
		mockMvc.perform(put("/api/product/updateProduct").contentType("application/json")
				.content(objectMapper.writeValueAsString(productDto))).andExpect(status().isBadRequest());
	}

	@Test
	public void updateProduct_invalidLoginId() throws Exception {
		ProductDto productDto = new ProductDto(product1.getId(), 200, 20.3, "new name", "blue", "updated", headOfDepartmentUserId, 0);
		mockMvc.perform(put("/api/product/updateProduct").contentType("application/json")
				.content(objectMapper.writeValueAsString(productDto))).andExpect(status().isBadRequest());
	}

	@Test
	public void deleteProduct() throws Exception {
		mockMvc.perform(delete("/api/product/deleteProduct/{productId}", product2.getId()).param("loginId",
				String.valueOf(adminUserId))).andExpect(status().isOk());
	}

	@Test
	public void deleteProduct_invalidProductId() throws Exception {
		mockMvc.perform(
				delete("/api/product/deleteProduct/{productId}", -1L).param("loginId", String.valueOf(adminUserId)))
				.andExpect(status().isBadRequest());
	}
 
	@Test
	public void deleteProduct_invalidLoginId() throws Exception {
		mockMvc.perform(delete("/api/product/deleteProduct/{productId}", product1.getId()).param("loginId",
				String.valueOf(headOfDepartmentUserId))).andExpect(status().isBadRequest());
	}
}
