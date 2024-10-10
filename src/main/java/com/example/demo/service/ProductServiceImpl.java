package com.example.demo.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.enums.ProductStatusEnum;
import com.example.demo.enums.RoleEnum;
import com.example.demo.enums.workflow.WFProcessesEnum;
import com.example.demo.exception.BusinessException;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.ProductTransactionHistory;
import com.example.demo.model.orm.workflow.product.WFProduct;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ProductTransactionHistoryRepository;
import com.example.demo.service.workflow.ProductWorkFlowService;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private ProductWorkFlowService productWorkFlowService;

	@Autowired
	private ProductTransactionHistoryRepository productTransactionHistoryRepository;

	@Override
	public Product findById(Long productId) {
		return productRepository.findById(productId)
				.orElseThrow(() -> new BusinessException("Cannot find product in DB"));
	}

	@Override
	public void save(WFProduct wfProduct, Long loginId) throws BusinessException {
		validateAuthRoles(loginId);
		validateProductMandatoryFields(wfProduct);
		Long processId = (wfProduct.getProductId() == null) ? WFProcessesEnum.ADD_PRODUCT.getCode()
				: validateAndGetUpdateProcessId(wfProduct);
		productWorkFlowService.initProductWorkFlow(processId, wfProduct, loginId);
	}

	@Override
	public void deleteProduct(Long productId, Long loginId) throws BusinessException {
		validateAuthRoles(loginId);
		validateProductExists(productId);
		WFProduct wfProduct = new WFProduct();
		wfProduct.setProductId(productId);
		productWorkFlowService.initProductWorkFlow(WFProcessesEnum.DELETE_PRODUCT.getCode(), wfProduct, loginId);
	}

	@Override
	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	@Override
	public void doApproveEffect(WFProduct wfProduct) {
		Product product = ProductMapper.INSTANCE.mapWFProduct(wfProduct);
		if (wfProduct.getStatus() == ProductStatusEnum.ADD.getCode()) {
			product.setCreationDate(new Date());
			productRepository.save(product);
		} else if (wfProduct.getStatus() == ProductStatusEnum.UPDATE.getCode()) {
			updateExistingProduct(wfProduct, product);
		} else if (wfProduct.getStatus() == ProductStatusEnum.DELETE.getCode()) {
			deleteExistingProduct(wfProduct);
		}
	}

	@Override
	public void updateProductQuantityWithoutAuth(Long productId, int newQuantity) throws BusinessException {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new BusinessException("Product not found."));
		product.setStockQuantity(newQuantity);
		productRepository.save(product);
	}

	private void updateExistingProduct(WFProduct wfProduct, Product product) {
		Product oldProduct = getExistingProduct(wfProduct.getProductId());
		product.setCreationDate(oldProduct.getCreationDate());
		createProductTransactionHistory(oldProduct, wfProduct.getStatus());
		productRepository.save(product);
	}

	private void deleteExistingProduct(WFProduct wfProduct) {
		Product oldProduct = getExistingProduct(wfProduct.getProductId());
		createProductTransactionHistory(oldProduct, wfProduct.getStatus());
		productRepository.delete(oldProduct);
	}

	private Product getExistingProduct(Long productId) {
		return productRepository.findById(productId).orElseThrow(() -> new BusinessException("Product not found."));
	}

	private void createProductTransactionHistory(Product oldProduct, Integer status) {
		ProductTransactionHistory transactionHistory = new ProductTransactionHistory(null, oldProduct.getId(),
				oldProduct.getProductName(), oldProduct.getPrice(), oldProduct.getColor(),
				oldProduct.getStockQuantity(), oldProduct.getDescription(), status, new Date());
		productTransactionHistoryRepository.save(transactionHistory);
	}

	private void validateProductMandatoryFields(WFProduct wfProduct) {
		if (wfProduct.getPrice() == null || wfProduct.getPrice() == 0 || wfProduct.getProductName() == null
				|| wfProduct.getProductName().isEmpty()) {
			throw new BusinessException("Product price and name should not be null or empty");
		}
	}

	private void validateAuthRoles(Long loginId) {
		Long roleId = userService.getUserById(loginId).get().getRoleId();
		if (roleId != RoleEnum.SUPER_ADMIN.getCode() && roleId != RoleEnum.ADMIN.getCode()) {
			throw new BusinessException("Product addition failed - Unauthorized");
		}
	}

	private void validateProductExists(Long productId) {
		productRepository.findById(productId).orElseThrow(() -> new BusinessException("Product not found."));
	}

	private Long validateAndGetUpdateProcessId(WFProduct wfProduct) {
		validateProductExists(wfProduct.getProductId());
		return WFProcessesEnum.UPDATE_PRODUCT.getCode();
	}
}
