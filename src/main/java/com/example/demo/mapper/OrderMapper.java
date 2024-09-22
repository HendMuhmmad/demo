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
import com.example.demo.model.orm.Vw_Order_Details;

@Mapper
@Component
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    public OrderDTO mapOrder(Order order);

    public Order mapOrderDto(OrderDetailsCreationDTO orderDto);
    

    default List<OrderResponseDto> mapOrders(List<Vw_Order_Details> orderDetailsList) {
    	List<Integer> orderIds = getOrderIds(orderDetailsList);
    	List<OrderResponseDto> orderResponseDtos = new ArrayList<OrderResponseDto>();
    	for (Integer orderId : orderIds) {
    	    // get array of Vw_Order_Details for each orderId
    	    List<Vw_Order_Details> orderDetails = getOrdersForOrderId(orderDetailsList, orderId);
    	    orderResponseDtos.add(OrderMapper.INSTANCE.mapOrder(orderDetails));	
    	    }
    	
    	return orderResponseDtos;
    }
    
    default OrderResponseDto mapOrder(List<Vw_Order_Details> orderDetails) {
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
    
    static List<Integer> getOrderIds(List<Vw_Order_Details> orderDetailsList) {
		List<Integer> orderIds = new ArrayList<Integer>();
		HashSet<Integer> orderIdsSet = new HashSet<Integer>();
		for (Vw_Order_Details Vw_order_detail : orderDetailsList) {
			orderIdsSet.add(Vw_order_detail.getOrderId());
		}
		orderIds.addAll(orderIdsSet);
		return orderIds;
	}

	static List<Vw_Order_Details> getOrdersForOrderId(List<Vw_Order_Details> orderDetailsList, int orderId) {
		List<Vw_Order_Details> orderDetails = new ArrayList<Vw_Order_Details>();
		for (Vw_Order_Details Vw_order_detail : orderDetailsList) {
			if (Vw_order_detail.getOrderId() == orderId) {
				orderDetails.add(Vw_order_detail);
			}
		}

		return orderDetails;
	}

}
