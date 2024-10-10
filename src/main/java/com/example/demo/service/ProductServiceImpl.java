package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.enums.RoleEnum;
import com.example.demo.enums.workflow.WFAssigneeRoleEnum;
import com.example.demo.enums.workflow.WFProcessesEnum;
import com.example.demo.enums.workflow.WFProductStatusEnum;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.orm.Product;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.workflow.WFProductService;
import com.example.demo.service.workflow.WFTaskDetailsService;

@Service
public class ProductServiceImpl implements ProductService {
	@Autowired
	public ProductRepository productRepository;

	@Autowired
	public UserService userService;
	
    @Autowired
    private WFProductService wfProductService;

    @Autowired
    private WFTaskDetailsService wfTaskDetailsService;
    
	@Override
	public Product findById(Long productId) {
		return productRepository.findById(productId)
				.orElseThrow(() -> new BusinessException("Cannot find product in DB"));

	}

	public void save(Product theProduct, Long loginId) throws BusinessException {
		Long roleId = userService.getUserById(loginId).get().getRoleId();
		if (theProduct.getPrice() == 0 || theProduct.getProductName() == null) {
			throw new BusinessException("Product price and name shoud not be null");
		}
		if (roleId == RoleEnum.SUPER_ADMIN.getCode() ) {
			saveAsSuperAdmin(theProduct,loginId);
		} else if (roleId == RoleEnum.ADMIN.getCode()) {
			saveAsAdmin(theProduct,loginId);
		}
		else {
			throw new BusinessException("Product addition failed - Unauthorized");
		}
	}
	
	public void saveAsSuperAdmin(Product theProduct, Long loginId) throws BusinessException{
		productRepository.save(theProduct);
	}
	
	public void saveAsAdmin(Product theProduct, Long loginId) throws BusinessException {
	    // Create process instance
	    Long assigneeId = userService.findRandomSuperAdminId();
	    boolean isNew = theProduct.getId() == null;
	    // Validate product and check for running tasks if not new
	    if (!isNew) validateProductAndCheckTasks(theProduct.getId());
	    // Determine workflow process and status based on product state
	    WFProcessesEnum process = isNew ? WFProcessesEnum.ADD_PRODUCT : WFProcessesEnum.UPDATE_PRODUCT;
	    WFProductStatusEnum status = isNew ? WFProductStatusEnum.ADDED : WFProductStatusEnum.UPDATED;

	    // Initialize workflow objects
	    wfProductService.initWorkflowObjects(theProduct, loginId, process, status, WFAssigneeRoleEnum.SUPERADMIN, assigneeId);
	}


	public void updateProductQuantity(Long productId, int newQuantity, Long loginId) throws BusinessException {
		Product product = productRepository.findById(productId).orElse(null);
		if (product != null) {
			// Update product stock quantity
			Product updatedProduct = clone(product,newQuantity);
			save(updatedProduct,loginId);
		} else {
			throw new BusinessException("No product found");
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
		if (roleId == RoleEnum.SUPER_ADMIN.getCode()) {
			deleteAsSuperAdmin(productId);
		} else if (roleId == RoleEnum.ADMIN.getCode()) {
			deleteAsAdmin(productId,loginId);
		} else {
			throw new BusinessException("Product addition failed - Unauthorized");
		}
	}

	private void deleteAsAdmin(Long productId, Long loginId) {
		Long assigneeId = userService.findRandomSuperAdminId();
		Product product = validateProductAndCheckTasks(productId);
		wfProductService.initWorkflowObjects(product,loginId,WFProcessesEnum.DELETE_PRODUCT,WFProductStatusEnum.DELETED,WFAssigneeRoleEnum.SUPERADMIN,assigneeId);
	}


	private void deleteAsSuperAdmin(Long productId) {
		validateProduct(productId);
		productRepository.deleteById(productId);
	}

	@Override
	public List<Product> getAllProduct() {
		return productRepository.findAll();
	}
	
	private Product validateProduct(Long productId) {
		// get initial product
		Product previousProduct = findById(productId);
		if (previousProduct == null) throw new BusinessException("Product does not exist");
		return previousProduct;
	}
	
	private Product validateProductAndCheckTasks(Long productId) throws BusinessException {
	    Product product = validateProduct(productId);
	    if (wfTaskDetailsService.hasOtherRunningTasks(productId)) {
	        throw new BusinessException("This product has other pending requests");
	    }
	    return product;
	}

    
    public Product clone(Product product, Integer newQuantity) {
        Product clonedProduct = new Product();
        clonedProduct.setId(product.getId()); 
        clonedProduct.setProductName(product.getProductName());
        clonedProduct.setPrice(product.getPrice());
        clonedProduct.setColor(product.getColor());
        clonedProduct.setStockQuantity(newQuantity);
        clonedProduct.setDescription(product.getDescription());
        clonedProduct.setCreationDate(product.getCreationDate()); 
        return clonedProduct;
    }


}
