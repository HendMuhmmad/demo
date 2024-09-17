package com.example.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import com.example.demo.model.dto.order.OrderDTO;
import com.example.demo.model.dto.orderDetails.OrderDetailsCreationDTO;
import com.example.demo.model.orm.Order;
import com.example.demo.model.orm.OrderDetails;



@Mapper
@Component
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    public OrderDTO mapOrder(Order order);
    
    public Order mapOrderDto(OrderDetailsCreationDTO orderDto);
}
