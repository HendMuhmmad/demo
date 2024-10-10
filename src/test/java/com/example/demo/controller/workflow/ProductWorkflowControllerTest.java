package com.example.demo.controller.workflow;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

import com.example.demo.enums.ProductStatusEnum;
import com.example.demo.enums.RoleEnum;
import com.example.demo.enums.workflow.WFActionEnum;
import com.example.demo.model.dto.workflow.WFTaskActionDto;
import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.User;
import com.example.demo.model.orm.workflow.WFActions;
import com.example.demo.model.orm.workflow.WFProcess;
import com.example.demo.model.orm.workflow.WFProcessGroup;
import com.example.demo.model.orm.workflow.product.WFProduct;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ProductTransactionHistoryRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.workflow.WFActionsRepository;
import com.example.demo.repository.workflow.WFInstanceRepository;
import com.example.demo.repository.workflow.WFProcessGroupRepository;
import com.example.demo.repository.workflow.WFProcessRepository;
import com.example.demo.repository.workflow.WFTaskRepository;
import com.example.demo.repository.workflow.product.WFProductRepository;
import com.example.demo.service.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
public class ProductWorkflowControllerTest {
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
	ProductService productService;

	@Autowired
	ProductTransactionHistoryRepository productTransactionHistoryRepository;

	@Autowired
	private WFProcessGroupRepository wfProcessGroupRepository;

	@Autowired
	private WFProcessRepository wfProcessRepository;

	@Autowired
	private WFActionsRepository wfActionRepository;

	private Long adminUserId;
	private Long superAdminUserId;

	@BeforeAll
	public void setUpWorkFlow() {
		WFProcessGroup savedProcessGroup = new WFProcessGroup();
		savedProcessGroup.setProcessGroupName("Products");
		wfProcessGroupRepository.save(savedProcessGroup);

		WFProcess savedProcessAdd = new WFProcess();
		savedProcessAdd.setName("Add Product");
		savedProcessAdd.setGroupId(savedProcessGroup.getId());
		wfProcessRepository.save(savedProcessAdd);

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

		adminUserId = userRepository.save(adminUser).getId();
		superAdminUserId = userRepository.save(superAdminUser).getId();

	}

	@AfterEach
	public void tearDown() {
		wfTaskRepository.deleteAllInBatch();
		wfProductRepository.deleteAllInBatch();
		wfInstanceRepository.deleteAllInBatch();
		userRepository.deleteAllInBatch();
		productTransactionHistoryRepository.deleteAllInBatch();
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
	public void testDoAddProductTaskActionApproved() throws Exception {
		initAddProductWorkFlow();
		doTaskActionApproved();
	}

	@Test
	public void testDoUpdateProductTaskActionApproved() throws Exception {
		initUpdateProductWorkFlow();
		doTaskActionApproved();
	}
	
	@Test
	public void testDoDeleteProductTaskActionApproved() throws Exception {
		initDeleteProductWorkFlow();
		doTaskActionApproved();
	}


	@Test
	public void testDoAddProductTaskActionRejected() throws Exception {
		initAddProductWorkFlow();
		doTaskActionRejected();
	}

	@Test
	public void testDoUpdateProductTaskActionRejected() throws Exception {
		initUpdateProductWorkFlow();
		doTaskActionRejected();
	}
	
	@Test
	public void testDoDeleteProductTaskActionRejected() throws Exception {
		initDeleteProductWorkFlow();
		doTaskActionRejected();
	}


	@Test
	public void testgetTaskById() throws Exception {
		initAddProductWorkFlow();
		Long taskId = wfTaskRepository.findByAssigneeIdAndActionIdIsNull(superAdminUserId).get(0).getId();
		mockMvc.perform(get("/api/product/workflow/task/" + taskId)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(String.valueOf(taskId)))
				.andExpect(jsonPath("$.assigneeId").value(String.valueOf(superAdminUserId)));
	}

	@Test
	public void testgetTasksByAssigneeId() throws Exception {
		initAddProductWorkFlow();
		mockMvc.perform(get("/api/product/workflow/tasks/" + superAdminUserId)).andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$[0].assigneeId").value(String.valueOf(superAdminUserId)));
	}
	
	
	@Test
	public void testRetrieveNonExistentTaskById() throws Exception {
	    Long nonExistentTaskId = -1L;
	    mockMvc.perform(get("/api/product/workflow/task/" + nonExistentTaskId))
	           .andExpect(status().isBadRequest());
	}

	@Test
	public void testRetrieveTasksByNonExistentAssigneeId() throws Exception {
	    Long nonExistentAssigneeId = -1L;
	    mockMvc.perform(get("/api/product/workflow/tasks/" + nonExistentAssigneeId))
	           .andExpect(status().isOk())
	           .andExpect(jsonPath("$").isArray())
	           .andExpect(jsonPath("$").isEmpty());
	}

	@Test
	public void testHandleTaskActionWithInvalidTaskId() throws Exception {
	    Long nonExistentTaskId = -1L;
	    WFTaskActionDto wfTaskActionDto = new WFTaskActionDto(
	    		nonExistentTaskId, WFActionEnum.APPROVE.getCode(), null, null, superAdminUserId);
	    mockMvc.perform(post("/api/product/workflow/doAction")
	            .contentType("application/json")
	            .content(objectMapper.writeValueAsString(wfTaskActionDto)))
	            .andExpect(status().isBadRequest());
	}

	@Test
	public void testRetrieveTasksByAssigneeIdWhenNoTasksExist() throws Exception {
	    mockMvc.perform(get("/api/product/workflow/tasks/" + superAdminUserId))
	           .andExpect(status().isOk())
	           .andExpect(jsonPath("$").isArray())
	           .andExpect(jsonPath("$").isEmpty());
	}
	
	@Test
	public void testHandleTaskActionByNonAssignee() throws Exception {
		initAddProductWorkFlow();
	    Long taskId = wfTaskRepository.findByAssigneeIdAndActionIdIsNull(superAdminUserId).get(0).getId();
	    WFTaskActionDto wfTaskActionDto = new WFTaskActionDto(
	            taskId, WFActionEnum.APPROVE.getCode(), null, null, adminUserId);
	    
	    mockMvc.perform(post("/api/product/workflow/doAction")
	            .contentType("application/json")
	            .content(objectMapper.writeValueAsString(wfTaskActionDto)))
	            .andExpect(status().isBadRequest());
	}

	private void initAddProductWorkFlow() {
		productService.save(
				new WFProduct(null, null, null, "name", 5.5, "color", 50, "desc", ProductStatusEnum.ADD.getCode()),
				adminUserId);

	}

	private void initUpdateProductWorkFlow() {
		Product savedProduct = productRepository
				.save(new Product(null, "t shirt", 55.5, "red", 80, "description", new Date()));
		productService.save(new WFProduct(null, savedProduct.getId(), null, "name", 5.5, "color", 50, "desc",
				ProductStatusEnum.UPDATE.getCode()), adminUserId);
	}

	private void initDeleteProductWorkFlow() {
		Product savedProduct = productRepository
				.save(new Product(null, "t shirt", 55.5, "red", 80, "description", new Date()));
		productService.deleteProduct(savedProduct.getId(), adminUserId);

	}

	private void doTaskActionRejected() throws Exception, JsonProcessingException {
		WFTaskActionDto wfTaskActionDto = new WFTaskActionDto(
				wfTaskRepository.findByAssigneeIdAndActionIdIsNull(superAdminUserId).get(0).getId(),
				WFActionEnum.REJECT.getCode(), null, "refuse notes",superAdminUserId);
		mockMvc.perform(post("/api/product/workflow/doAction").contentType("application/json")
				.content(objectMapper.writeValueAsString(wfTaskActionDto))).andExpect(status().isOk());
	}

	private void doTaskActionApproved() throws Exception, JsonProcessingException {
		WFTaskActionDto wfTaskActionDto = new WFTaskActionDto(
				wfTaskRepository.findByAssigneeIdAndActionIdIsNull(superAdminUserId).get(0).getId(),
				WFActionEnum.APPROVE.getCode(), null, null,superAdminUserId);
		mockMvc.perform(post("/api/product/workflow/doAction").contentType("application/json")
				.content(objectMapper.writeValueAsString(wfTaskActionDto))).andExpect(status().isOk());
	}

}
