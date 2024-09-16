package com.example.demo.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.mapper.OrderListMapper;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.model.dto.order.OrderDTO;
import com.example.demo.model.dto.order.OrderListDTO;
import com.example.demo.model.orm.Order;
import com.example.demo.model.orm.OrderDetails;
import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.User;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderDetailsService orderDetailsService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProductRepository productRepository;

	@Override
	public List<OrderListDTO> getAllUserOrders(int userId) {
        return OrderListMapper.MAPPER.mapOrders(orderRepository.findByUserId(userId));
	}

	@Override
	public OrderDTO createOrder(int userId, List<OrderDetails> orderDetails) {
		// get user by Id
		Optional<User> result = userRepository.findById(userId);
		User theUser = null;
		if (result.isPresent()) {
			theUser = result.get();
		} else {
			throw new RuntimeException("Did not find user id - " + userId);
		}

		// check role
		// if not customer return exception
		if (theUser.getRoleId() != 4) {
			throw new RuntimeException("User is not a customer - " + userId);
		}

		// create an order
		Order order = new Order();
		order.setUserId(userId);
		order.setOrderNumber(generateUUID()); 
		order.setTransactionDate(new Date());
		orderRepository.save(order);
		// create orderDetails
		for (OrderDetails orderDetail : orderDetails) {
			// validate product
			Product product = validateProduct(orderDetail.getProduct_id());
			// validate order detail
			validateOrderDetailAndReturnProduct(orderDetail, product);
			 int remainingQuantity = product.getStockQuantity() - orderDetail.getQuantity();
			// update quantity
			product.setStockQuantity(remainingQuantity);
			// need repository to update
			productRepository.save(product);
			orderDetail.setOrderId(order.getId());
			orderDetailsService.createOrderDetail(orderDetail);
			double orderDetailPrice = product.getPrice() * orderDetail.getQuantity();
			order.setTotalPrice(order.getTotalPrice() + orderDetailPrice);
		}

		return OrderMapper.INSTANCE.mapOrder(order);
	}

	
	
	private Product validateProduct(int productId) {
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

	private void validateOrderDetailAndReturnProduct(OrderDetails orderDetail, Product product) {
		int quantityRequested = orderDetail.getQuantity();
		// validate quantity
		int stockQuantity = product.getStockQuantity();
		if (stockQuantity < quantityRequested) {
			throw new RuntimeException("Not enough stock available. Requested " + quantityRequested + " and only "
					+ stockQuantity + " available");

		}
	}
	
    private String generateUUID() {
        // Generate a UUID
        UUID uuid = UUID.randomUUID();
        
        // Convert the UUID to a string
        String uuidAsString = uuid.toString();
        
        // Return the UUID string
        return uuidAsString;
    }


}
