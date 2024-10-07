package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.example.demo.exception.BusinessException;
import com.example.demo.model.orm.workflow.WFTaskDetails;
import com.example.demo.repository.workflow.WFTaskDetailsRepository;

@SpringBootTest
public class WFProductServiceTest {
	
	@Autowired
    private WFProductService wfProductService;
    
    @MockBean
    private WFTaskDetailsRepository wfTaskDetailsRepository;
    
	private WFTaskDetails taskDetails;

    @BeforeEach
    void setUp() {
      
        taskDetails = new WFTaskDetails();
        taskDetails.setTaskId(1L);
        taskDetails.setAssigneeId(1L);
        taskDetails.setDescription("Sample Task");
    }


    @Test
    void testGetTasksByUserId_Success() {
        long userId = 1L;
        List<WFTaskDetails> mockTasks = Collections.singletonList(taskDetails);

        System.out.println(mockTasks.get(0));
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
        assertTrue(result.isEmpty());  // Expecting no tasks
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

}
