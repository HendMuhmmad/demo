package com.example.demo.mapper;

import java.util.ArrayList;
import java.util.HashSet;
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
import com.example.demo.model.orm.OrderDetailsData;

@Mapper
@Component
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    public OrderDTO mapOrder(Order order);

    public Order mapOrderDto(OrderDetailsCreationDTO orderDto);
    

    default List<OrderResponseDto> mapOrders(List<OrderDetailsData> orderDetailsList) {
    	List<Long> orderIds = getOrderIds(orderDetailsList);
    	List<OrderResponseDto> orderResponseDtos = new ArrayList<OrderResponseDto>();
    	for (Long orderId : orderIds) {
    	    // get array of Vw_Order_Details for each orderId
    	    List<OrderDetailsData> orderDetails = getOrdersForOrderId(orderDetailsList, orderId);
    	    orderResponseDtos.add(OrderMapper.INSTANCE.mapOrder(orderDetails));	
    	    }
    	
    	return orderResponseDtos;
    }
    
    default OrderResponseDto mapOrder(List<OrderDetailsData> orderDetails) {
		if (orderDetails.size() == 0) {
			return null;
		}
		CustomerDto c = CustomerMapper.INSTANCE.toDto(orderDetails.get(0));
		List<ProductDto> p = ProductMapper.INSTANCE.mapViews(orderDetails);
		double totalPrice = IntStream.range(0, orderDetails.size())
				.mapToDouble(i -> orderDetails.get(i).getTotalPrice())
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
    
    static List<Long> getOrderIds(List<OrderDetailsData> orderDetailsList) {
		List<Long> orderIds = new ArrayList<Long>();
		HashSet<Long> orderIdsSet = new HashSet<Long>();
		for (OrderDetailsData Vw_order_detail : orderDetailsList) {
			orderIdsSet.add(Vw_order_detail.getOrderId());
		}
		orderIds.addAll(orderIdsSet);
		return orderIds;
	}

	static List<OrderDetailsData> getOrdersForOrderId(List<OrderDetailsData> orderDetailsList, Long orderId) {
		List<OrderDetailsData> orderDetails = new ArrayList<OrderDetailsData>();
		for (OrderDetailsData Vw_order_detail : orderDetailsList) {
			if (Vw_order_detail.getOrderId() == orderId) {
				orderDetails.add(Vw_order_detail);
			}
		}

		return orderDetails;
	}

}
