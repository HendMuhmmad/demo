package com.example.demo.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.enums.RoleEnum;
import com.example.demo.enums.workflow.WFProcessesEnum;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.dto.TaskRequestDto;
import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.WFProduct;
import com.example.demo.model.orm.workflow.ProductTrnsHistory;
import com.example.demo.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {
	@Autowired
	public ProductRepository productRepository;

	@Autowired
	public UserService userService;
	
	@Autowired
	public WFProductService wfProductService;
	
	@Autowired
	public ProductTrnsHistoryService productTrnsHistoryService;

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
	public String request(Product theProduct, Long loginId,Long operation) {
	    validateProduct(theProduct);
		if(operation == WFProcessesEnum.ADD_PRODUCT.getCode()) {
			return requestCreateProduct(theProduct,loginId);
		}
		else if(operation == WFProcessesEnum.UPDATE_PRODUCT.getCode()) {
			return requestUpdateProduct(theProduct,loginId);
		}
		else {
			throw new BusinessException("Opeation error");
		}
	}
	private String requestCreateProduct(Product theProduct, Long loginId) {
	    Long roleId = userService.getUserById(loginId).get().getRoleId();
	    if (roleId == RoleEnum.SUPER_ADMIN.getCode()) {
	    	productRepository.save(theProduct);
			return "Your product is created successfully"; 
	    }
	    else if(roleId == RoleEnum.ADMIN.getCode()) {
	    	wfProductService.initWFProduct(theProduct, loginId, WFProcessesEnum.ADD_PRODUCT.getCode());
	    	return "Your request is sent successfully";
	    }
	    else {
			throw new BusinessException("You are not authorized to create a product");
	    }
	}
	private String requestUpdateProduct(Product theProduct, Long loginId) {
		validateProductFound(theProduct);
	    Long roleId = userService.getUserById(loginId).get().getRoleId();
	    if(roleId == RoleEnum.ADMIN.getCode()||roleId == RoleEnum.SUPER_ADMIN.getCode()) {
	    	wfProductService.initWFProduct(theProduct, loginId, WFProcessesEnum.UPDATE_PRODUCT.getCode());
	    	return "Your request is sent successfully";
	    }
	    else {
			throw new BusinessException("You are not authorized to update a product");
	    }
	}

	@Override
	public String requestDeleteProduct(Long productId, Long loginId) {
		Long roleId = userService.getUserById(loginId).get().getRoleId();

		if (roleId == RoleEnum.SUPER_ADMIN.getCode() || roleId == RoleEnum.ADMIN.getCode()) {
			Optional<Product> productTemp = productRepository.findById(productId);
			if(!productTemp.isPresent()) {
				throw new BusinessException("There is no product with this Id");
			}
			Product product = productTemp.get();
			wfProductService.initWFProduct(product, loginId, WFProcessesEnum.DELETE_PRODUCT.getCode());
			return  "Your request is sent successfully";
		} else {
			throw new BusinessException("Product addition failed - Unauthorized");
		}
	}
	
	private void validateProduct(Product theProduct) {
		if (theProduct.getPrice() == 0 || theProduct.getProductName() == null) {
			throw new BusinessException("Product price and name should not be null");
		}
	}
	private void validateProductFound(Product product) {
		Optional<Product> productOptional = productRepository.findById(product.getId());
	    if (!productOptional.isPresent()) {
			throw new BusinessException("There is no product with this Id");
	    }
	}
	


}
