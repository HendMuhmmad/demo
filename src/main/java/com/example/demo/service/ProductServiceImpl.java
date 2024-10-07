package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.enums.RoleEnum;
import com.example.demo.enums.workflow.WFProcessesEnum;
import com.example.demo.enums.workflow.WFStatusEnum;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.dto.TaskRequestDto;
import com.example.demo.model.orm.Product;
import com.example.demo.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {
	@Autowired
	public ProductRepository productRepository;

	@Autowired
	public UserService userService;
	
	@Autowired
	public WFProductService wfProductService;

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

	@Transactional
	public String request(Product theProduct, Long loginId) {
		if (theProduct.getPrice() == 0 || theProduct.getProductName() == null) {
			throw new BusinessException("Product price and name shoud not be null");
		}
	    Long roleId = userService.getUserById(loginId).get().getRoleId();
	    System.out.println(theProduct);
	    if (roleId == RoleEnum.SUPER_ADMIN.getCode()) {
//	    	theProduct.setWfStatus(WFStatusEnum.APPROVED.getCode());
	    	productRepository.save(theProduct);
			return "Your product is done successfully"; 
	    }
	    else if(roleId == RoleEnum.ADMIN.getCode()) {
//	    	theProduct.setWfStatus(WFStatusEnum.UNDER_APPROVAL.getCode());
//	    	productRepository.save(theProduct);
	    	wfProductService.initWFProduct(theProduct.getId(), loginId, WFProcessesEnum.ADD_PRODUCT.getCode());
	    	return "Your request is sent successfully";
	    }
	    else {
			throw new BusinessException("You are not authorizted to create a product");
	    }
	}

	@Override
	@Transactional
	public void respondToRequest(TaskRequestDto taskRequest) {
		long productId = wfProductService.respondToRequest(taskRequest);
		Optional<Product> tempProduct = productRepository.findById(productId);
		if(!tempProduct.isPresent()) {
			throw new BusinessException("There isn't a product with this id");
		}
		Product product = tempProduct.get();
		if(product.getWfStatus() != 0) {
			throw new BusinessException("This task is already done");			
		}
		if(taskRequest.getIsApproved()) {
			product.setWfStatus(WFStatusEnum.APPROVED.getCode());
		}else {
			product.setWfStatus(WFStatusEnum.REJECTED.getCode());
		}
		productRepository.save(product);
		
	}
	


}
