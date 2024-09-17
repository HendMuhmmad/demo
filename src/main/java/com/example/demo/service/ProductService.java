package com.example.demo.service;


import com.example.demo.model.orm.Product;



public interface ProductService {
	public Product findbyId(int theId);
	public int save(Product theProduct,  int loginId);
	public boolean deleteProduct(int productId, int loginId);
	public boolean updateProductQuantity(int productId, int newQuantity, int loginId);
}
