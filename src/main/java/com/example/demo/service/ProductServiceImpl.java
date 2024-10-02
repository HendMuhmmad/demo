package com.example.demo.service;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.enums.RoleEnum;
import com.example.demo.enums.workflow.WFAsigneeRoleEnum;
import com.example.demo.enums.workflow.WFInstanceStatusEnum;
import com.example.demo.enums.workflow.WFProcessesEnum;
import com.example.demo.enums.workflow.WFStatusEnum;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.User;
import com.example.demo.model.orm.workflow.WFInstance;
import com.example.demo.model.orm.workflow.WFProduct;
import com.example.demo.model.orm.workflow.WFTask;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.workflow.WFInstanceRepository;
import com.example.demo.repository.workflow.WFProductRepository;
import com.example.demo.repository.workflow.WFTaskRepository;

@Service
public class ProductServiceImpl implements ProductService {
	@Autowired
	public ProductRepository productRepository;

	@Autowired
	public UserService userService;
	
	@Autowired
	public UserRepository userRepository;

	@Autowired
    private WFInstanceRepository wfInstanceRepository;
	
    @Autowired
    private WFTaskRepository wfTaskRepository;
    
    @Autowired
    private WFProductRepository wfProductRepository;
    
	@Override
	public Product findbyId(Long productId) {
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
			// Return an error response indicating unauthorized access
			throw new BusinessException("Product addition failed - Unauthorized");
		}
	}
	
	public void saveAsSuperAdmin(Product theProduct, Long loginId) throws BusinessException{
		theProduct.setWfStatus(1L); // approved 
		productRepository.save(theProduct);
	}
	
	public void saveAsAdmin(Product theProduct, Long loginId) throws BusinessException{
		// create process instance
		WFInstance wfInstance = createWFInstance(WFProcessesEnum.ADD_PRODUCT.getCode(),loginId);
		// pick random superadmin
		Long assigneeId = findRandomSuperAdminId();
		// create process instance task
		createWFTask(wfInstance.getId(), assigneeId, WFAsigneeRoleEnum.SUPERADMIN.getRole());;
		// create product with wf_status 0
		theProduct.setWfStatus(WFStatusEnum.UNDERAPPROVAL.getCode()); // underapproval 
		// save product
		theProduct = productRepository.save(theProduct);
		// create wfProduct
		createWFProduct(theProduct.getId(),wfInstance.getId());
		
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
		return productRepository.findByWfStatus(1L);
	}
	
    private WFInstance createWFInstance(Long processId, Long requesterId) {
        WFInstance wfInstance = new WFInstance();
        wfInstance.setProcessId(processId);
        wfInstance.setRequesterId(requesterId);
        wfInstance.setRequestDate(new Date()); 
        wfInstance.setStatus(WFInstanceStatusEnum.RUNNING.getCode());

        return wfInstanceRepository.save(wfInstance); // Save the instance
    }
    
    private WFTask createWFTask(Long instanceId, Long assigneeId, String assigneeRole) {
        WFTask wfTask = new WFTask(instanceId, assigneeId, assigneeRole, new Date());
        return wfTaskRepository.save(wfTask); // Save the task
    }
    
    private WFProduct createWFProduct(Long productId,Long instanceId) {
		WFProduct wfProduct = new WFProduct(productId,instanceId);
        return wfProductRepository.save(wfProduct); // Save the task
    }
    
    public Long findRandomSuperAdminId() throws BusinessException {
		List<User> superAdmins = userRepository.findByRoleId(RoleEnum.SUPER_ADMIN.getCode());
        if (superAdmins.isEmpty()) 
        	throw new BusinessException("No SuperAdmin found");
        Random random = new Random();
        int randomIndex = random.nextInt(superAdmins.size());
        return superAdmins.get(randomIndex).getId();
    }
    private WFInstance getWFInstance(Long id) {
        return wfInstanceRepository.findById(id).orElse(null); // Fetch the instance by ID
    }
    public List<WFTask> getTasksByInstanceId(Long instanceId) {
        return wfTaskRepository.findByInstanceIdOrderByIdAsc(instanceId); // Fetch tasks by instance ID
    }

}
