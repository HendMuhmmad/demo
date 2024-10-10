package com.example.demo.service.workflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.example.demo.enums.ProductStatusEnum;
import com.example.demo.enums.RoleEnum;
import com.example.demo.enums.workflow.WFActionEnum;
import com.example.demo.enums.workflow.WFAsigneeRoleEnum;
import com.example.demo.enums.workflow.WFInstanceStatusEnum;
import com.example.demo.enums.workflow.WFProcessesEnum;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.dto.workflow.WFTaskDto;
import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.User;
import com.example.demo.model.orm.workflow.WFInstance;
import com.example.demo.model.orm.workflow.WFTask;
import com.example.demo.model.orm.workflow.product.WFProduct;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ProductTransactionHistoryRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.workflow.WFInstanceRepository;
import com.example.demo.repository.workflow.WFTaskRepository;
import com.example.demo.repository.workflow.product.WFProductRepository;
import com.example.demo.service.ProductService;

@SpringBootTest
public class ProductWorkFlowServiceTest {

	@Autowired
	private ProductWorkFlowService productWorkFlowService;

	@MockBean
	private WFInstanceRepository wfInstanceRepository;

	@MockBean
	private WFTaskRepository wfTaskRepository;

	@MockBean
	private WFProductRepository wfProductRepository;

	@MockBean
	private ProductRepository productRepository;

	@MockBean
	private ProductService productService;

	@MockBean
	private ProductTransactionHistoryRepository productTransactionHistoryRepository;

	@MockBean
	private UserRepository userRepository;

    private static final Long SUPER_ADMIN_ID = 1L;
    private static final Long ADMIN_ID = 2L;
    private static final Long TASK_ID_1 = 1L;
    private static final Long TASK_ID_2 = 2L;
    private static final Long INSTANCE_ID = 1L;
    private static final Long PRODUCT_ID = 2L;

    
	@Test
	void testInitAddProductWorkFlow() {
		WFProduct wfProduct = createWFProduct(null, null, ProductStatusEnum.ADD.getCode());
		initProductWorkFlow(WFProcessesEnum.ADD_PRODUCT.getCode(), wfProduct);
	}

	@Test
	void testInitUpdateProductWorkFlow() {
		WFProduct wfProduct = createWFProduct(null, PRODUCT_ID, ProductStatusEnum.UPDATE.getCode());
		initProductWorkFlow(WFProcessesEnum.UPDATE_PRODUCT.getCode(), wfProduct);
	}

	@Test
	void testInitDeleteProductWorkFlow() {
		WFProduct wfProduct = createWFProduct(null, PRODUCT_ID, ProductStatusEnum.DELETE.getCode());
		initProductWorkFlow(WFProcessesEnum.DELETE_PRODUCT.getCode(), wfProduct);
	}

	@Test
	void testDoTaskActionApproved() {
		setupMockWorkflow(TASK_ID_1, SUPER_ADMIN_ID,INSTANCE_ID,WFProcessesEnum.ADD_PRODUCT.getCode(),null);
		productWorkFlowService.doTaskAction(WFActionEnum.APPROVE.getCode(), TASK_ID_1, "Notes", null, SUPER_ADMIN_ID);
		verifyTaskActionFlow(WFActionEnum.APPROVE.getCode(), WFInstanceStatusEnum.DONE.getCode(), true);
	}

	@Test
	void testDoTaskActionRejected() {
		setupMockWorkflow(TASK_ID_1, SUPER_ADMIN_ID, INSTANCE_ID,WFProcessesEnum.ADD_PRODUCT.getCode(),null);
		productWorkFlowService.doTaskAction(WFActionEnum.REJECT.getCode(), TASK_ID_1, null, "Refuse Reasons", SUPER_ADMIN_ID);
		verifyTaskActionFlow(WFActionEnum.REJECT.getCode(), WFInstanceStatusEnum.DONE.getCode(), false);
	}

	@Test
	void testDoTaskActionRejectedNoRefuseNotes() {
		doTaskRejectActionWithError(null);
	}

	@Test
	void testDoTaskActionRejectedEmptyRefuseNotes() {
		doTaskRejectActionWithError("");
	}

	@Test
	void testGetWFTaskById() {
		Long wfInstanceId = setupMockWorkflow(TASK_ID_1, SUPER_ADMIN_ID, INSTANCE_ID,WFProcessesEnum.ADD_PRODUCT.getCode(),null);
		WFTaskDto<WFProduct> result = productWorkFlowService.getProductWorkFlowTaskById(TASK_ID_1);
		verifyTaskRetrieval(TASK_ID_1, wfInstanceId, result);
	}

	@Test
	void testGetWFTasksByAssigneeId() {
		List<WFTask> wfTasks = mockMultipleWFTasksForAssignee(SUPER_ADMIN_ID, List.of(TASK_ID_1, TASK_ID_2));
		when(wfTaskRepository.findByAssigneeIdAndActionIdIsNull(SUPER_ADMIN_ID)).thenReturn(wfTasks);
		List<WFTaskDto<WFProduct>> result = productWorkFlowService.getProductWorkFlowTasksByAsigneeId(SUPER_ADMIN_ID);
		assertEquals(2, result.size());
	}

	@Test
    void testInitProductWorkFlowNoSuperAdmin() {
        when(userRepository.findByRoleId(RoleEnum.SUPER_ADMIN.getCode())).thenReturn(List.of());
		when(wfInstanceRepository.save(any(WFInstance.class))).thenReturn(createWFInstance(null, WFProcessesEnum.ADD_PRODUCT.getCode(), ADMIN_ID, WFInstanceStatusEnum.RUNNING.getCode()));
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            productWorkFlowService.initProductWorkFlow(WFProcessesEnum.ADD_PRODUCT.getCode(), new WFProduct(),ADMIN_ID);
        });
        assertEquals("No Super Admins were found", exception.getMessage());
    }
	
	@Test
    void testInitProductWorkFlowWithCurrentluRunningRequests() {
		mockSuperAdmin(SUPER_ADMIN_ID);
		when(wfInstanceRepository.save(any(WFInstance.class))).thenReturn(createWFInstance(INSTANCE_ID, WFProcessesEnum.UPDATE_PRODUCT.getCode(), ADMIN_ID, WFInstanceStatusEnum.RUNNING.getCode()));
		when(wfProductRepository.checkRunningWorkFlowRequests(PRODUCT_ID, INSTANCE_ID)).thenReturn(List.of(createWFProduct(INSTANCE_ID, PRODUCT_ID, ProductStatusEnum.UPDATE.getCode())));
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            productWorkFlowService.initProductWorkFlow(WFProcessesEnum.UPDATE_PRODUCT.getCode(),createWFProduct(null, PRODUCT_ID, null),ADMIN_ID);
        });
        assertEquals("Product is already attached to another workflow instance", exception.getMessage());
    }

	@Test
    void testDoTaskActionTaskNotFound() {
        when(wfTaskRepository.findById(TASK_ID_1)).thenReturn(Optional.empty());
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            productWorkFlowService.doTaskAction(WFActionEnum.APPROVE.getCode(), TASK_ID_1, "Notes", null, SUPER_ADMIN_ID);
        });
        assertEquals("WFTask not found", exception.getMessage());
    }

	@Test
    void testDoTaskActionInstanceNotFound() {
        when(wfTaskRepository.findById(TASK_ID_1)).thenReturn(Optional.of(createWFTask(TASK_ID_1, SUPER_ADMIN_ID, INSTANCE_ID)));
        when(wfInstanceRepository.findById(INSTANCE_ID)).thenReturn(Optional.empty());
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            productWorkFlowService.doTaskAction(WFActionEnum.APPROVE.getCode(), TASK_ID_1, "Notes", null,SUPER_ADMIN_ID);
        });
        assertEquals("WFInstance not found", exception.getMessage());
    }
	
	@Test
	void testDoTaskActionNotOwner() {
	    setupMockWorkflow(TASK_ID_1, ADMIN_ID, INSTANCE_ID,WFProcessesEnum.ADD_PRODUCT.getCode(),null);
	    BusinessException exception = assertThrows(BusinessException.class, () -> {
	        productWorkFlowService.doTaskAction(WFActionEnum.APPROVE.getCode(), TASK_ID_1, "Notes", null, SUPER_ADMIN_ID);
	    });
	    assertEquals("You are not the owner of this task", exception.getMessage());
	}

	@Test
	void testDoTaskActionWorkflowNotRunning() {
	    setupMockWorkflow(TASK_ID_1, SUPER_ADMIN_ID, INSTANCE_ID,WFProcessesEnum.ADD_PRODUCT.getCode(),null);
	    when(wfInstanceRepository.findById(INSTANCE_ID)).thenReturn(Optional.of(createWFInstance(INSTANCE_ID, WFProcessesEnum.ADD_PRODUCT.getCode(), ADMIN_ID, WFInstanceStatusEnum.DONE.getCode())));
	    BusinessException exception = assertThrows(BusinessException.class, () -> {
	        productWorkFlowService.doTaskAction(WFActionEnum.APPROVE.getCode(), TASK_ID_1, "Notes", null, SUPER_ADMIN_ID);
	    });
	    assertEquals("Workflow is not in a valid state for this action", exception.getMessage());
	}

	private void initProductWorkFlow(Long processId, WFProduct wfProduct) {
		mockSuperAdmin(SUPER_ADMIN_ID);
		when(wfInstanceRepository.save(any(WFInstance.class))).thenReturn(createWFInstance(null, processId, ADMIN_ID, WFInstanceStatusEnum.RUNNING.getCode()));
		productWorkFlowService.initProductWorkFlow(processId, wfProduct, ADMIN_ID);
		verifySavedWFInstance(processId);
		verifySavedWFProduct(wfProduct.getStatus());
		verifySavedWFTask(SUPER_ADMIN_ID);
	}

	private void doTaskRejectActionWithError(String refuseNotes) {
		setupMockWorkflow(TASK_ID_1, SUPER_ADMIN_ID, INSTANCE_ID,WFProcessesEnum.ADD_PRODUCT.getCode(),null);
		BusinessException exception = assertThrows(BusinessException.class, () -> {
			productWorkFlowService.doTaskAction(WFActionEnum.REJECT.getCode(), TASK_ID_1, null, refuseNotes, SUPER_ADMIN_ID);
		});
		assertEquals("Refuse Notes cannot be null or empty", exception.getMessage());
		verify(productRepository, never()).save(any(Product.class));
	}

	private Long setupMockWorkflow(Long taskId, Long assigneeId, Long instanceId,Long processId, Long productId) {
		WFTask wfTask = createWFTask(taskId, assigneeId, instanceId);
		WFInstance wfInstance = createWFInstance(instanceId, processId, ADMIN_ID, WFInstanceStatusEnum.RUNNING.getCode());
		WFProduct wfProduct = createWFProduct(wfInstance.getId(), productId, null);
		mockRepositoryCallsForWorkflow(taskId, wfTask, wfInstance, wfProduct);
		return wfInstance.getId();
	}

	private List<WFTask> mockMultipleWFTasksForAssignee(Long assigneeId, List<Long> taskIds) {
		return taskIds.stream().map(taskId -> {
			WFTask wfTask = createWFTask(taskId, assigneeId, INSTANCE_ID);
			mockRepositoryCallsForWorkflow(taskId, wfTask,
					createWFInstance(INSTANCE_ID, WFProcessesEnum.ADD_PRODUCT.getCode(), ADMIN_ID, WFInstanceStatusEnum.RUNNING.getCode()),
					createWFProduct(taskId, null, null));
			return wfTask;
		}).toList();
	}

	private void verifyTaskActionFlow(Long expectedAction, int expectedStatus, boolean shouldApprove) {
		verifyWFInstanceStatusUpdated(expectedStatus);
		verifyWFTaskActionUpdated(expectedAction);
		if (shouldApprove) {
			verify(productService, times(1)).doApproveEffect(any(WFProduct.class));
		} else {
			verify(productService, never()).doApproveEffect(any(WFProduct.class));
		}
	}

	private void verifyTaskRetrieval(Long taskId, Long wfInstanceId, WFTaskDto<WFProduct> result) {
		verify(wfTaskRepository).findById(taskId);
		verify(wfProductRepository).findByWfInstanceId(wfInstanceId);
		assertNotNull(result);
		assertNotNull(result.getRequest());
		assertEquals(taskId, result.getId());
		assertEquals(wfInstanceId, result.getInstanceId());
	}

	private WFTask createWFTask(Long taskId, Long assigneeId, Long instanceId) {
		WFTask wfTask = new WFTask();
		wfTask.setId(taskId);
		wfTask.setAssigneeId(assigneeId);
		wfTask.setInstanceId(instanceId);
		wfTask.setAssigneeRole(WFAsigneeRoleEnum.PRODUCT_MANAGER.getCode());
		return wfTask;
	}

	private WFInstance createWFInstance(Long instanceId, Long processId, Long loginId, Integer instanceStatus) {
		WFInstance wfInstance = new WFInstance();
		wfInstance.setId(instanceId);
		wfInstance.setProcessId(processId);
		wfInstance.setRequesterId(loginId);
		wfInstance.setStatus(instanceStatus);
		wfInstance.setRequestDate(new Date());
		return wfInstance;
	}

	private WFProduct createWFProduct(Long wfInstanceId, Long productId, Integer status) {
		WFProduct wfProduct = new WFProduct();
		wfProduct.setWfInstanceId(wfInstanceId);
		wfProduct.setProductId(productId);
		wfProduct.setStatus(status);
		return wfProduct;
	}

	private void mockRepositoryCallsForWorkflow(Long taskId, WFTask wfTask, WFInstance wfInstance, WFProduct wfProduct) {
        when(wfTaskRepository.findById(taskId)).thenReturn(Optional.of(wfTask));
        when(wfInstanceRepository.findById(wfTask.getInstanceId())).thenReturn(Optional.of(wfInstance));
        when(wfProductRepository.findByWfInstanceId(wfTask.getInstanceId())).thenReturn(Optional.of(wfProduct));
    }

	private void mockSuperAdmin(Long superAdminId) {
		User superAdmin = new User();
		superAdmin.setId(superAdminId);
		superAdmin.setRoleId(RoleEnum.SUPER_ADMIN.getCode());
		when(userRepository.findByRoleId(RoleEnum.SUPER_ADMIN.getCode())).thenReturn(List.of(superAdmin));
	}

	private void verifySavedWFInstance(Long processId) {
		ArgumentCaptor<WFInstance> wfInstanceCaptor = ArgumentCaptor.forClass(WFInstance.class);
		verify(wfInstanceRepository).save(wfInstanceCaptor.capture());
		assertEquals(processId, wfInstanceCaptor.getValue().getProcessId());
	}

	private void verifySavedWFProduct(Integer productStatus) {
		ArgumentCaptor<WFProduct> wfProductCaptor = ArgumentCaptor.forClass(WFProduct.class);
		verify(wfProductRepository).save(wfProductCaptor.capture());
		assertEquals(productStatus, wfProductCaptor.getValue().getStatus());
	}

	private void verifySavedWFTask(Long superAdminId) {
		ArgumentCaptor<WFTask> wfTaskCaptor = ArgumentCaptor.forClass(WFTask.class);
		verify(wfTaskRepository).save(wfTaskCaptor.capture());
		assertEquals(superAdminId, wfTaskCaptor.getValue().getAssigneeId());
	}

	private void verifyWFInstanceStatusUpdated(int expectedStatus) {
		ArgumentCaptor<WFInstance> wfInstanceCaptor = ArgumentCaptor.forClass(WFInstance.class);
		verify(wfInstanceRepository).save(wfInstanceCaptor.capture());
		assertEquals(expectedStatus, (int) wfInstanceCaptor.getValue().getStatus());
	}

	private void verifyWFTaskActionUpdated(Long expectedAction) {
		ArgumentCaptor<WFTask> wfTaskCaptor = ArgumentCaptor.forClass(WFTask.class);
		verify(wfTaskRepository).save(wfTaskCaptor.capture());
		assertEquals(expectedAction, wfTaskCaptor.getValue().getActionId());
	}
}
