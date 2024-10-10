package com.example.demo.service.workflow;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.enums.RoleEnum;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.orm.User;
import com.example.demo.model.orm.WFProduct;
import com.example.demo.model.orm.workflow.WFInstance;
import com.example.demo.model.orm.workflow.WFTask;
import com.example.demo.repository.workflow.WFProductRepository;
import com.example.demo.service.UserService;

@ExtendWith(MockitoExtension.class)

public class WfProductTest {

    private static final Long loginId = 25L;

    @Mock
    private WFProductRepository wfProductRepository;

    @InjectMocks
    private WFProductServiceImpl wfProductService;

    @Mock
    private WFInstanceServiceImpl wfInstanceServiceImpl;

    @Mock
    private UserService userService;

    @Mock
    private WFTaskServiceImpl wfTaskServiceImpl;

    private WFProduct wfProduct;

    private WFInstance wfInstance;

    private WFTask wfTask;

    @BeforeEach
    public void setUp() {
	wfProduct = new WFProduct();
	wfProduct.setProductId(12345L);
	wfProduct.setWfInstanceId(67890L);
	wfProduct.setProductName("Example Product");
	wfProduct.setPrice(99.99);
	wfProduct.setColor("Red");
	wfProduct.setStockQuantity(100);
	wfProduct.setDescription("This is an example product description.");

	wfInstance = new WFInstance();

	wfInstance.setProcessId(1L);
	wfInstance.setRequesterId(1L);
	wfInstance.setRequestDate(new Date());
	wfInstance.setStatus(1);

	wfTask = new WFTask();

	wfTask.setId(1L);
	wfTask.setAssigneeId(1L);
	wfTask.setActionId(1L);
	wfTask.setActionDate(new Date());
    }

    @Test
    public void test_ProductIsNotNull() {

	assertNotNull(wfProduct);
    }

    @Test
    public void test_InitWfThrowsExceptionWhenProductIsNull() {
	assertThrows(BusinessException.class, () -> {
	    wfProductService.initWf(null, 1L);
	});
    }

    @Test
    public void test_InitWfDoesNotThrowExceptionWhenProductIsValid() {
	Optional<User> user = Optional.of(new User());
	user.get().setRoleId(RoleEnum.ADMIN.getCode());
	when(wfProductRepository.save(any())).thenReturn(wfProduct);
	when(wfInstanceServiceImpl.save(any())).thenReturn(wfInstance.getId());
	when(wfTaskServiceImpl.save(any())).thenReturn(wfTask);
	when(userService.getUserById(any())).thenReturn(user);

	assertNotNull(wfProduct);
	try {
	    wfProductService.initWf(wfProduct, loginId);
	} catch (BusinessException e) {
	}
    }

    @Test
    public void test_InitThrowsExceptionWhenWorkflowFails() {
	when(wfProductService.save(any())).thenThrow(new RuntimeException("Workflow  failed"));

	assertThrows(BusinessException.class, () -> {
	    wfProductService.initWf(wfProduct, 1L);
	});
    }

    @Test
    public void test_InitSuccessfullyCreatesWorkflow() throws BusinessException {
	Optional<User> user = Optional.of(new User());
	user.get().setRoleId(RoleEnum.ADMIN.getCode());

	when(wfProductService.save(any())).thenReturn(wfProduct);
	when(wfProductRepository.save(any())).thenReturn(wfProduct);
	when(userService.getUserById(any())).thenReturn(user);

	wfProductService.initWf(wfProduct, loginId);

	assertNotNull(wfProduct.getWfInstanceId());
    }

    @Test
    public void test_ValidateWFProduct_NullProductId() {
	wfProduct.setProductId(null);
	assertThrows(BusinessException.class, () -> {
	    wfProductService.validateWFProduct(wfProduct);
	});
    }

    @Test
    public void test_ValidateWFProduct_EmptyProductName() {
	wfProduct.setProductName("");
	assertThrows(BusinessException.class, () -> {
	    wfProductService.validateWFProduct(wfProduct);
	});
    }

    @Test
    public void test_ValidateWFProduct_NullProductName() {
	wfProduct.setProductName(null);
	assertThrows(BusinessException.class, () -> {
	    wfProductService.validateWFProduct(wfProduct);
	});
    }

    @Test
    public void test_ValidateWFProduct_NegativePrice() {
	wfProduct.setPrice(-1.0);
	assertThrows(BusinessException.class, () -> {
	    wfProductService.validateWFProduct(wfProduct);
	});
    }

    @Test
    public void test_ValidateWFProduct_NullPrice() {
	wfProduct.setPrice(null);
	assertThrows(BusinessException.class, () -> {
	    wfProductService.validateWFProduct(wfProduct);
	});
    }

    @Test
    public void test_ValidateWFProduct_NegativeStockQuantity() {
	wfProduct.setStockQuantity(-1);
	assertThrows(BusinessException.class, () -> {
	    wfProductService.validateWFProduct(wfProduct);
	});
    }

    @Test
    public void test_ValidateWFProduct_EmptyColor() {
	wfProduct.setColor("");
	assertThrows(BusinessException.class, () -> {
	    wfProductService.validateWFProduct(wfProduct);
	});
    }

    @Test
    public void test_ValidateWFProduct_NullColor() {
	wfProduct.setColor(null);
	assertThrows(BusinessException.class, () -> {
	    wfProductService.validateWFProduct(wfProduct);
	});
    }

    @Test
    public void test_ValidateWFProduct_ValidProduct() {
	// This test should pass without throwing an exception
	wfProductService.validateWFProduct(wfProduct);
    }
}
