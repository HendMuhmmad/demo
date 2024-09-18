package com.example.demo.mapper;

import java.util.List;
import java.util.stream.IntStream;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import com.example.demo.model.dto.CustomerDto;
import com.example.demo.model.dto.OrderResponseDto;
import com.example.demo.model.dto.ProductDto;
import com.example.demo.model.dto.order.OrderDTO;
import com.example.demo.model.dto.orderDetails.OrderDetailsCreationDTO;
import com.example.demo.model.orm.Order;
import com.example.demo.model.orm.Vw_Order_Details;

@Mapper
@Component
public interface OrderMapper {

	OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

	public OrderDTO mapOrder(Order order);

	public Order mapOrderDto(OrderDetailsCreationDTO orderDto);

	default OrderResponseDto mapOrder(List<Vw_Order_Details> orderDetails) {
		if (orderDetails.size() == 0) {
			return null;
		}
		CustomerDto c = CustomerMapper.INSTANCE.toDto(orderDetails.get(0));
		List<ProductDto> p = ProductMapper.INSTANCE.mapViews(orderDetails);
		double totalPrice = IntStream.range(0, orderDetails.size())
				.mapToDouble(i -> orderDetails.get(i).getOrderedQuantity() * orderDetails.get(i).getProductPrice())
				.sum();
		OrderResponseDto orderResponseDto = new OrderResponseDto();
		orderResponseDto.setOrderId(orderDetails.get(0).getOrderId());
		orderResponseDto.setOrderNumber(orderDetails.get(0).getOrderNumber());
		orderResponseDto.setUserId(orderDetails.get(0).getUserId());
		orderResponseDto.setTransactionDate(orderDetails.get(0).getTransactionDate());
		orderResponseDto.setCustomer(c);
		orderResponseDto.setItems(p);
		orderResponseDto.setTotalPrice(totalPrice);;
											
		return orderResponseDto;

	}

}
