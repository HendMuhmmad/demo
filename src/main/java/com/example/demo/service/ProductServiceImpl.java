package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.enums.RoleEnum;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.orm.Product;
import com.example.demo.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {
	@Autowired
	public ProductRepository productRepository;

	@Autowired
	public UserService userService;

	@Override
	public Product findbyId(Long productId) {
		return productRepository.findById(productId)
				.orElseThrow(() -> new BusinessException("Cannot find product in DB"));

	}

	public Long save(Product theProduct, Long loginId) throws BusinessException {
		Long roleId = userService.getUserById(loginId).get().getRoleId();
		if (theProduct.getPrice() == 0 || theProduct.getProductName() == null) {
			throw new BusinessException("Product price and name shoud not be null");
		}
		if (roleId == RoleEnum.SUPER_ADMIN.getCode() || roleId == RoleEnum.ADMIN.getCode()) {
			Product product = productRepository.save(theProduct);
			// Return a success response with the product ID
			return product.getId();
		} else {
			// Return an error response indicating unauthorized access
			throw new BusinessException("Product addition failed - Unauthorized");
		}
	}

	public void updateProductQuantity(Long productId, int newQuantity, Long loginId) throws BusinessException {
		Long roleId = userService.getUserById(loginId).get().getRoleId();
		if (roleId == RoleEnum.SUPER_ADMIN.getCode() || roleId == RoleEnum.ADMIN.getCode()) {
			Product product = productRepository.findById(productId).orElse(null);
			if (product != null) {
				// Update product stock quantity
				product.setStockQuantity(newQuantity);
				productRepository.save(product);
			} else {
				throw new BusinessException("Product not found.");
			}
		} else {
			throw new BusinessException("Product addition failed - Unauthorized");
		}
	}

	public void updateProductQuantityWithOutAuth(Long productId, int newQuantity) throws BusinessException {
		Product product = productRepository.findById(productId).orElse(null);
		if (product != null) {
			// Update product stock quantity
			product.setStockQuantity(newQuantity);
			productRepository.save(product);
		} else {
			throw new BusinessException("Product not found.");
		}
	}

	@Override
	public void deleteProduct(Long productId, Long loginId) throws BusinessException {
		Long roleId = userService.getUserById(loginId).get().getRoleId();
		if (!productRepository.findById(productId).isPresent())
			throw new BusinessException("Product not found.");

		if (roleId == RoleEnum.SUPER_ADMIN.getCode() || roleId == RoleEnum.ADMIN.getCode()) {
			productRepository.deleteById(productId);
		} else {
			throw new BusinessException("Product addition failed - Unauthorized");
		}
	}

	@Override
	public List<Product> getAllProduct() {
		return productRepository.findAll();
	}

}
