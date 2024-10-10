package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.example.demo.enums.ProductStatusEnum;
import com.example.demo.enums.RoleEnum;
import com.example.demo.enums.workflow.WFProcessesEnum;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.ProductTransactionHistory;
import com.example.demo.model.orm.User;
import com.example.demo.model.orm.workflow.product.WFProduct;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ProductTransactionHistoryRepository;
import com.example.demo.repository.workflow.product.WFProductRepository;
import com.example.demo.service.workflow.ProductWorkFlowService;

@SpringBootTest
public class ProductServiceTest {

    @MockBean
    private ProductWorkFlowService productWorkFlowService;

    @Autowired
    private ProductService productService;

    @MockBean
    private WFProductRepository wfProductRepository;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private ProductTransactionHistoryRepository productTransactionHistoryRepository;
    
    Long productId = 1L;

    @Test
    public void saveProduct_ValidAdminId() {
        verifySaveProductWithRole(RoleEnum.ADMIN);
    }

    @Test
    public void saveProduct_ValidSuperAdminId() {
        verifySaveProductWithRole(RoleEnum.SUPER_ADMIN);
    }

    @Test
    public void saveProduct_InvalidCustomerId() {
        verifySaveProductWithRoleAndExpectException(RoleEnum.CUSTOMER);
    }

    @Test
    public void saveProduct_InvalidHeadOfDepartmentId() {
        verifySaveProductWithRoleAndExpectException(RoleEnum.HEAD_OF_DEPARTMENT);
    }

    @Test
    public void saveProduct_withProductPriceIsNull() {
        assertProductSaveThrowsException(new WFProduct(null, null, null, "name", 0.0, "color", 50, "desc", null), RoleEnum.ADMIN);
    }

    @Test
    public void saveProduct_withProductNameIsNull() {
        assertProductSaveThrowsException(new WFProduct(null, null, null, null, 5.5, "color", 50, "desc", null), RoleEnum.ADMIN);
    }

  
    @Test
    public void updateProduct_invalidProductId() {
        User adminUser = createUserWithRoleId(RoleEnum.ADMIN.getCode());
        Mockito.when(userService.getUserById(Mockito.any(Long.class))).thenReturn(Optional.of(adminUser));
        assertThrows(BusinessException.class, () -> {
            productService.save(dummyWFProduct(productId, ProductStatusEnum.UPDATE.getCode()), adminUser.getId());
        });
    }

    @Test
    public void updateProduct_ValidAdminId() {
        verifyUpdateProductWithRole(productId, RoleEnum.ADMIN);
    }

    @Test
    public void updateProduct_ValidSuperAdminId() {
        verifyUpdateProductWithRole(productId, RoleEnum.SUPER_ADMIN);
    }

    @Test
    public void updateProduct_InvalidCustomerId() {
        verifyUpdateProductWithRoleAndExpectException(productId, RoleEnum.CUSTOMER);
    }

    @Test
    public void updateProduct_InvalidHeadOfDepartmentId() {
        verifyUpdateProductWithRoleAndExpectException(productId, RoleEnum.HEAD_OF_DEPARTMENT);
    }

    @Test
    public void doAddApproveEffect() {
        productService.doApproveEffect(dummyWFProduct(null, ProductStatusEnum.ADD.getCode()));
        verifySavedProduct(null);
        verify(productTransactionHistoryRepository, Mockito.never()).save(Mockito.any(ProductTransactionHistory.class));
    }

    @Test
    public void doUpdateApproveEffect() {
        Mockito.when(productRepository.findById(Mockito.eq(productId))).thenReturn(dummyOptionalProduct(productId));
        productService.doApproveEffect(dummyWFProduct(productId, ProductStatusEnum.UPDATE.getCode()));
        verifySavedProduct(productId);
        verifySavedProductTransactionHistory(productId, ProductStatusEnum.UPDATE.getCode());
    }

    @Test
    public void doDeleteApproveEffect() {
        Mockito.when(productRepository.findById(Mockito.eq(productId))).thenReturn(dummyOptionalProduct(productId));
        productService.doApproveEffect(dummyWFProduct(productId, ProductStatusEnum.DELETE.getCode()));
        verify(productRepository, times(1)).delete(dummyProduct(productId));
        verifySavedProductTransactionHistory(productId, ProductStatusEnum.DELETE.getCode());
    }

    @Test
    public void deleteProduct_ValidAdminId() {
        verifyDeleteProductWithRole(RoleEnum.ADMIN);
    }

    @Test
    public void deleteProduct_ValidSuperAdminId() {
        verifyDeleteProductWithRole(RoleEnum.SUPER_ADMIN);
    }

    @Test
    public void deleteProduct_InvalidCustomerId() {
        verifyDeleteProductWithRoleAndExpectException(RoleEnum.CUSTOMER);
    }

    @Test
    public void deleteProduct_InvalidHeadOfDepartmentId() {
        verifyDeleteProductWithRoleAndExpectException(RoleEnum.HEAD_OF_DEPARTMENT);
    }

    @Test
    public void deleteProduct_invalidProductId() {
        User adminUser = createUserWithRoleId(RoleEnum.ADMIN.getCode());
        Mockito.when(userService.getUserById(Mockito.any(Long.class))).thenReturn(Optional.of(adminUser));
        Mockito.when(productRepository.findById(Mockito.eq(productId))).thenReturn(dummyOptionalProduct(productId));
        assertThrows(BusinessException.class, () -> {
            productService.deleteProduct(8L, adminUser.getId());
        });
    }

    @Test
    public void deleteProduct_noProducts() {
        User adminUser = createUserWithRoleId(RoleEnum.ADMIN.getCode());
        Mockito.when(userService.getUserById(Mockito.any(Long.class))).thenReturn(Optional.of(adminUser));
        Mockito.when(productRepository.findById(Mockito.eq(1L))).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> {
            productService.deleteProduct(1L, adminUser.getId());
        });
    }

    private void verifySaveProductWithRole(RoleEnum role) {
        User user = createUserWithRoleId(role.getCode());
        prepareProductSaveMocks(user);
        productService.save(dummyWFProduct(null, ProductStatusEnum.ADD.getCode()), user.getId());
        Mockito.verify(productWorkFlowService, times(1)).initProductWorkFlow(WFProcessesEnum.ADD_PRODUCT.getCode(), dummyWFProduct(null, ProductStatusEnum.ADD.getCode()), user.getId());
    }

    private void verifySaveProductWithRoleAndExpectException(RoleEnum role) {
        User user = createUserWithRoleId(role.getCode());
        Mockito.when(wfProductRepository.save(Mockito.any(WFProduct.class))).thenReturn(dummyWFProduct(null, ProductStatusEnum.ADD.getCode()));
        Mockito.when(userService.getUserById(Mockito.any(Long.class))).thenReturn(Optional.of(user));
        assertThrows(BusinessException.class, () -> {
            productService.save(dummyWFProduct(null, ProductStatusEnum.ADD.getCode()), user.getId());
        });
    }

    private void verifyUpdateProductWithRole(Long productId, RoleEnum role) {
        User user = createUserWithRoleId(role.getCode());
        prepareProductUpdateMocks(productId, user);
        productService.save(dummyWFProduct(productId, ProductStatusEnum.UPDATE.getCode()), user.getId());
        Mockito.verify(productWorkFlowService, times(1)).initProductWorkFlow(WFProcessesEnum.UPDATE_PRODUCT.getCode(), dummyWFProduct(productId, ProductStatusEnum.UPDATE.getCode()), user.getId());
    }

    private void verifyUpdateProductWithRoleAndExpectException(Long productId, RoleEnum role) {
        User user = createUserWithRoleId(role.getCode());
        Mockito.when(userService.getUserById(Mockito.any(Long.class))).thenReturn(Optional.of(user));
        Mockito.when(productRepository.findById(Mockito.eq(productId))).thenReturn(dummyOptionalProduct(productId));
        assertThrows(BusinessException.class, () -> {
            productService.save(dummyWFProduct(productId, ProductStatusEnum.UPDATE.getCode()), user.getId());
        });
    }

    private void verifyDeleteProductWithRole(RoleEnum role) {
        User user = createUserWithRoleId(role.getCode());
        WFProduct wfProduct = new WFProduct();
        wfProduct.setProductId(productId);
        Mockito.when(userService.getUserById(Mockito.any(Long.class))).thenReturn(Optional.of(user));
        Mockito.when(productRepository.findById(Mockito.eq(productId))).thenReturn(dummyOptionalProduct(productId));
        Mockito.doNothing().when(productWorkFlowService).initProductWorkFlow(Mockito.any(Long.class), Mockito.any(), Mockito.any());
        productService.deleteProduct(productId, user.getId());
        Mockito.verify(productWorkFlowService, times(1)).initProductWorkFlow(WFProcessesEnum.DELETE_PRODUCT.getCode(), wfProduct, user.getId());
    }

    private void verifyDeleteProductWithRoleAndExpectException(RoleEnum role) {
        User user = createUserWithRoleId(role.getCode());
        Mockito.when(userService.getUserById(Mockito.any(Long.class))).thenReturn(Optional.of(user));
        Mockito.when(productRepository.findById(Mockito.eq(productId))).thenReturn(dummyOptionalProduct(productId));
        assertThrows(BusinessException.class, () -> {
            productService.deleteProduct(productId, user.getId());
        });
    }

    private void assertProductSaveThrowsException(WFProduct wfProduct, RoleEnum role) {
        User user = createUserWithRoleId(role.getCode());
        Mockito.when(userService.getUserById(Mockito.any(Long.class))).thenReturn(Optional.of(user));
        assertThrows(BusinessException.class, () -> {
            productService.save(wfProduct, user.getId());
        });
    }

    private void verifySavedProduct(Long productId) {
        ArgumentCaptor<Product> argumentCaptor = ArgumentCaptor.forClass(Product.class);
        Mockito.verify(productRepository, times(1)).save(argumentCaptor.capture());
        Product savedProduct = argumentCaptor.getValue();
        assertEquals(productId, savedProduct.getId());
    }

    private void verifySavedProductTransactionHistory(Long productId, Integer status) {
    	Product product = dummyProduct(productId);
        ArgumentCaptor<ProductTransactionHistory> argumentCaptor = ArgumentCaptor.forClass(ProductTransactionHistory.class);
        Mockito.verify(productTransactionHistoryRepository, times(1)).save(argumentCaptor.capture());
        ProductTransactionHistory productTrnsHistory = argumentCaptor.getValue();
        assertEquals(productTrnsHistory.getProductId(), product.getId());
		assertEquals(productTrnsHistory.getColor(), product.getColor());
		assertEquals(productTrnsHistory.getDescription(), product.getDescription());
		assertEquals(productTrnsHistory.getPrice(), product.getPrice());
		assertEquals(productTrnsHistory.getProductName(), product.getProductName());
		assertEquals(productTrnsHistory.getStockQuantity(), product.getStockQuantity());
		assertEquals(productTrnsHistory.getStatus(), status);
    }

    private void prepareProductSaveMocks(User user) {
        Mockito.when(userService.getUserById(Mockito.any(Long.class))).thenReturn(Optional.of(user));
        Mockito.when(wfProductRepository.save(Mockito.any(WFProduct.class))).thenReturn(dummyWFProduct(null, ProductStatusEnum.ADD.getCode()));
    }

    private void prepareProductUpdateMocks(Long productId, User user) {
        Mockito.when(userService.getUserById(Mockito.any(Long.class))).thenReturn(Optional.of(user));
        Mockito.when(productRepository.findById(Mockito.eq(productId))).thenReturn(dummyOptionalProduct(productId));
    }

    private WFProduct dummyWFProduct(Long productId, Integer status) {
        WFProduct wfProduct = new WFProduct();
        wfProduct.setProductId(productId);
        wfProduct.setStatus(status);
        wfProduct.setProductName("name");
        wfProduct.setPrice(5.5);
        return wfProduct;
    }

    private Optional<Product> dummyOptionalProduct(Long productId) {
        return Optional.of(dummyProduct(productId));
    }

    private Product dummyProduct(Long productId) {
        Product product = new Product();
        product.setId(productId);
        product.setProductName("name");
        product.setPrice(5.5);
        return product;
    }

    private User createUserWithRoleId(Long roleId) {
        User user = new User();
        user.setId(1L);
        user.setRoleId(roleId);
        return user;
    }
}
