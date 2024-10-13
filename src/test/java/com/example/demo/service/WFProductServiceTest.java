package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.example.demo.enums.workflow.WFActionEnum;
import com.example.demo.enums.workflow.WFInstanceStatusEnum;
import com.example.demo.enums.workflow.WFProcessesEnum;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.dto.TaskRequestDto;
import com.example.demo.model.orm.WFProduct;
import com.example.demo.model.orm.workflow.WFInstance;
import com.example.demo.model.orm.workflow.WFTask;
import com.example.demo.model.orm.workflow.WFTaskDetails;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.workflow.WFInstanceRepository;
import com.example.demo.repository.workflow.WFProductRepository;
import com.example.demo.repository.workflow.WFTaskDetailsRepository;
import com.example.demo.repository.workflow.WFTaskRepository;

@SpringBootTest
public class WFProductServiceTest {
	
	@Autowired
    private WFProductService wfProductService;
    
    @MockBean
    private WFTaskDetailsRepository wfTaskDetailsRepository;

    @MockBean
    private WFTaskRepository wfTaskRepository;
    
    @MockBean
    private WFInstanceRepository wfInstanceRepository;
    
    @MockBean
    private WFProductRepository wfProductRepository;

    @MockBean
    private ProductRepository productRepository;

    private WFTaskDetails taskDetails;
    private WFTask wfTask;
    private WFProduct wfProduct;
    private WFInstance wfInstance;

    @BeforeEach
    void setUp() {
        taskDetails = new WFTaskDetails();
        taskDetails.setTaskId(1L);
        taskDetails.setAssigneeId(1L);
        taskDetails.setDescription("Sample Task");

        wfTask = new WFTask();
        wfTask.setId(1L);
        wfTask.setInstanceId(1L);
        wfTask.setAssigneeId(1L);
        
        wfProduct = new WFProduct();
        wfProduct.setProductId(1L);
        wfProduct.setWfInstanceId(1L);
        wfProduct.setStatus(1L);
        wfProduct.setPrice(50.0);
    
        wfInstance = new WFInstance();
        wfInstance.setId(1L);
        wfInstance.setStatus(WFInstanceStatusEnum.RUNNING.getCode());
    }


    @Test
    void testGetTasksByUserId_Success() {
        long userId = 1L;
        List<WFTaskDetails> mockTasks = Collections.singletonList(taskDetails);

        when(wfTaskDetailsRepository.findByAssigneeId(userId)).thenReturn(mockTasks);

        List<WFTaskDetails> result = wfProductService.getTasksByUserId(userId);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Sample Task", result.get(0).getDescription());
    }

    @Test
    void testGetTasksByUserId_NoTasksFound() {
        long userId = 1L;

        when(wfTaskDetailsRepository.findByAssigneeId(userId)).thenReturn(Collections.emptyList());

        List<WFTaskDetails> result = wfProductService.getTasksByUserId(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetTaskByTaskId_Success() {
        long taskId = 1L;

        when(wfTaskDetailsRepository.findById(taskId)).thenReturn(Optional.of(taskDetails));

        WFTaskDetails result = wfProductService.getTaskByTaskId(taskId);

        assertNotNull(result);
        assertEquals("Sample Task", result.getDescription());
    }

    @Test
    void testGetTaskByTaskId_TaskNotFound() {
        long taskId = 1L;

        when(wfTaskDetailsRepository.findById(taskId)).thenReturn(Optional.empty());

        BusinessException thrown = assertThrows(BusinessException.class, () -> {
            wfProductService.getTaskByTaskId(taskId);
        });

        assertEquals("Task with ID " + taskId + " not found", thrown.getMessage());
    }

    @Test
    void testRespondToRequest_Success() {
        TaskRequestDto taskRequest = new TaskRequestDto();
        taskRequest.setTaskId(1L);
        taskRequest.setUserId(1L);
        taskRequest.setApproved(true);
        
        when(wfTaskRepository.findById(1L)).thenReturn(Optional.of(wfTask));
        when(wfProductRepository.findByWfInstanceId(1L)).thenReturn(Optional.of(wfProduct));
        when(wfInstanceRepository.findById(1L)).thenReturn(Optional.of(wfInstance));
    
        wfProductService.respondToRequest(taskRequest);

        // Validate the response
        assertEquals((long) WFActionEnum.APPROVED.getCode(), (long) wfTask.getActionId());
        verify(wfTaskRepository, Mockito.times(1)).save(wfTask);
        verify(wfInstanceRepository, Mockito.times(1)).save(wfInstance);
    }

    @Test
    void testRespondToRequest_TaskNotFound() {
        TaskRequestDto taskRequest = new TaskRequestDto();
        taskRequest.setTaskId(1L);
        taskRequest.setUserId(1L);

        when(wfTaskRepository.findById(1L)).thenReturn(Optional.empty());

        BusinessException thrown = assertThrows(BusinessException.class, () -> {
            wfProductService.respondToRequest(taskRequest);
        });

        assertEquals("There isn't task with this id", thrown.getMessage());
    }

    @Test
    void testRespondToRequest_UnauthorizedUser() {
        TaskRequestDto taskRequest = new TaskRequestDto();
        taskRequest.setTaskId(1L);
        taskRequest.setUserId(2L);  

        when(wfTaskRepository.findById(1L)).thenReturn(Optional.of(wfTask));

        BusinessException thrown = assertThrows(BusinessException.class, () -> {
            wfProductService.respondToRequest(taskRequest);
        });

        assertEquals("You don't have the right to respond to this task", thrown.getMessage());
    }

    @Test
    void testRespondToRequest_TaskAlreadyDone() {
        TaskRequestDto taskRequest = new TaskRequestDto();
        taskRequest.setTaskId(1L);
        taskRequest.setUserId(1L);
        
        wfTask.setActionId(WFActionEnum.APPROVED.getCode());  // Task already done

        when(wfTaskRepository.findById(1L)).thenReturn(Optional.of(wfTask));

        BusinessException thrown = assertThrows(BusinessException.class, () -> {
            wfProductService.respondToRequest(taskRequest);
        });

        assertEquals("This task is already done", thrown.getMessage());
    }
}
