package com.example.demo.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.model.dto.CustomerDto;
import com.example.demo.model.dto.OrderResponseDto;
import com.example.demo.model.dto.ProductDto;
import com.example.demo.model.orm.Order;
import com.example.demo.model.orm.OrderDetails;
import com.example.demo.model.orm.Product;
import com.example.demo.model.orm.User;
import com.example.demo.repository.OrderDetailsRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;

@Mapper(componentModel = "spring")
@Component
public abstract class OrderAutowireMapper {

	@Autowired
	public UserRepository customerRepository;
	
	@Autowired
	public ProductRepository productRepository;
	
	@Autowired 
	public OrderDetailsRepository orderDetailsRepository;
	public static final OrderAutowireMapper MAPPER = Mappers.getMapper( OrderAutowireMapper.class );

	public OrderResponseDto mapOrder(Order order) {
		
		if ( order == null) {
			return null;
		}
		Optional<User> u = customerRepository.findById(order.getUserId());
		User user = null;
		if (u.isPresent()) {
			user = u.get();
		} else {
			throw new RuntimeException("User does not exist. id - "+ order.getUserId() );
		}
		List<OrderDetails> orderDetails = orderDetailsRepository.findByOrderId(order.getId());
		List<ProductDto> productDtos = new ArrayList<ProductDto>();
		
		for (OrderDetails orderDetail:orderDetails) {
			Optional<Product> p = productRepository.findById(orderDetail.getProduct_id());
			Product product = null;
			if (p.isPresent()) {
				product = p.get();
			} else {
				throw new RuntimeException("User does not exist. id - "+ order.getUserId() );
			}
			productDtos.add(ProductMapper.INSTANCE.mapProductAndOrderDetails(product, orderDetail));
		}
		CustomerDto c = CustomerMapper.INSTANCE.toDto(user);
		double totalPrice = IntStream.range(0, productDtos.size())
				.mapToDouble(i -> productDtos.get(i).getOrderedQuantity() * productDtos.get(i).getPrice())
				.sum();
		OrderResponseDto orderResponseDto = new OrderResponseDto();
		orderResponseDto.setOrderId(orderDetails.get(0).getOrderId());
		orderResponseDto.setOrderNumber(order.getOrderNumber());
		orderResponseDto.setUserId(order.getUserId());
		orderResponseDto.setTransactionDate(order.getTransactionDate());
		orderResponseDto.setCustomer(c);
		orderResponseDto.setItems(productDtos);
		orderResponseDto.setTotalPrice(totalPrice);
											
		return orderResponseDto;

	}

}
