package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.enums.RoleEnum;
import com.example.demo.model.orm.Product;
import com.example.demo.repository.ProductRepository;


@Service
public class ProductServiceImpl implements ProductService {
	@Autowired
	public ProductRepository productRepository;
	
	
	public ProductServiceImpl(ProductRepository productRepo) {
		this.productRepository = productRepo;
	}


	@Override
	public Product findbyId(int theId) {
		// TODO Auto-generated method stub
		Optional<Product> result= productRepository.findById(theId);
		Product theProduct =null;
		if(result.isPresent()) {
			theProduct= result.get(); 
		}else {
			throw new RuntimeException("can not find product in DB");
		}
		return theProduct;
	}

	@Override
	public int save(Product theProduct, int loginId) {
		// TODO Auto-generated method stub
		if(loginId == RoleEnum.SUPER_ADMIN.getCode() || loginId ==RoleEnum.ADMIN.getCode()) {
			Product product=productRepository.save(theProduct);
			return product.getId();
		}
		return -1;

	}
	@Override
	public boolean updateProductQuantity(int productId, int newQuantity, int loginId) {
		if(loginId == RoleEnum.SUPER_ADMIN.getCode() || loginId ==RoleEnum.ADMIN.getCode() ) {
	        Product product = productRepository.findById(productId).orElse(null);

	        if (product != null) {
	            // Update product stock quantity
	            product.setStockQuantity(newQuantity);
	            productRepository.save(product);
	            return true;
	        } else {
	            return false; // Product not found
	        }
		}
		return false;
	 }
	

	@Override
	public boolean deleteProduct(int productId, int loginId) {
		if(loginId == RoleEnum.SUPER_ADMIN.getCode() || loginId ==RoleEnum.ADMIN.getCode() ) {
			productRepository.deleteById(productId);
			return true;
		}
		return false;
	}

}
