package com.example.demo.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.model.dto.order.OrderListDTO;
import com.example.demo.model.orm.Order;
import com.example.demo.model.orm.OrderDetails;
import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.User;
import com.example.demo.repository.OrderDetailsRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;



@Mapper(componentModel = "spring")
public abstract class OrderListMapper {
	@Autowired
	OrderDetailsRepository orderDetailsRepository;
	@Autowired
	ProductRepository productRepository;


	@Autowired
	UserRepository userRepository;
	
	public static final OrderListMapper MAPPER = Mappers.getMapper(OrderListMapper.class);
//    CustomerMapper customerMapper = Mappers.getMapper(CustomerMapper.class);
//    OrderDetailsListMapper orderDetailsListMapper = Mappers.getMapper(OrderDetailsListMapper.class);
    public List<OrderListDTO> mapOrders(List<Order> orders) {
    	List<OrderListDTO> ordersDTO = new ArrayList<OrderListDTO>();
    	for(Order order:orders) {
    		ordersDTO.add(mapOrder(order));
    	}
    	return ordersDTO;
    }
    
    public OrderListDTO mapOrder(Order order) {
    	List<OrderDetails> orderDetails = orderDetailsRepository.findByOrderId(order.getId());
    	OrderListDTO orderListDTO = new OrderListDTO();
    	orderListDTO.setOrderNumber(order.getOrderNumber());
    	User user = getUser(order.getUserId());
    	orderListDTO.setCustomer(CustomerMapper.MAPPER.mapCustomer(user));
    	orderListDTO.setItems(OrderDetailsListMapper.MAPPER.mapOrderDetailsLists(orderDetails));
    	orderListDTO.setTransaction_date(order.getTransactionDate());
    	orderListDTO.setTotalPrice(0);
    	for (OrderDetails orderDetail : orderDetails) {
    		Product product = getProduct(orderDetail.getProduct_id());
			double orderDetailPrice = product.getPrice() * orderDetail.getQuantity();
			order.setTotalPrice(order.getTotalPrice() + orderDetailPrice);
		}
    	return orderListDTO;
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

	private User getUser(int userId) {
		// validate presence of product
		Optional<User> result = userRepository.findById(userId);
		User theUser = null;
		if (result.isPresent()) {
			theUser = result.get();
		} else {
			throw new RuntimeException("Did not find product id - " + userId);
		}

		return theUser;
	}
//    public Order mapOrderDto(OrderDetailsCreationDTO orderDto);
}