package com.example.demo.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.model.dto.orderDetails.OrderDetailsListDTO;
import com.example.demo.model.orm.OrderDetails;
import com.example.demo.model.orm.Product;
import com.example.demo.repository.ProductRepository;



@Mapper(componentModel = "spring")
public abstract class OrderDetailsListMapper {
	@Autowired
	private ProductRepository productRepository;
	public static final OrderDetailsListMapper MAPPER = Mappers.getMapper(OrderDetailsListMapper.class);
    public List<OrderDetailsListDTO> mapOrderDetailsLists(List<OrderDetails> orderDetails){
    	 List<OrderDetailsListDTO> list = new ArrayList<OrderDetailsListDTO>();
    	for (OrderDetails orderDetail:orderDetails) {
    		list.add(mapOrderDetailsList(orderDetail));
    	}
    	return list;
    }
    
    public OrderDetailsListDTO mapOrderDetailsList(OrderDetails orderDetail) {
    	OrderDetailsListDTO orderDetailsListDTO = new OrderDetailsListDTO();
    	Product product = getProduct(orderDetail.getProduct_id());
    	orderDetailsListDTO.setColor(product.getColor());
    	orderDetailsListDTO.setDescription(product.getDescription());
    	orderDetailsListDTO.setCreation_date(product.getCreationDate());
    	orderDetailsListDTO.setPrice(product.getPrice());
    	orderDetailsListDTO.setProduct_name(product.getProductName());
    	orderDetailsListDTO.setQuantity(orderDetail.getQuantity());
    	orderDetailsListDTO.setStock_quantity(product.getStockQuantity());
    	return orderDetailsListDTO;
    }
	private Product getProduct(int productId) {
		// validate presence of product
		Optional<Product> result = productRepository.findById(productId);
		Product theProduct = null;
		if (result.isPresent()) {
			theProduct = result.get();
		} else {
			throw new RuntimeException("Did not find product id - " + productId);
		}

		return theProduct;
	}

}
