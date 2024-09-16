package com.example.demo.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.example.demo.model.dto.orderDetails.OrderDetailsCreationDTO;
import com.example.demo.model.orm.OrderDetails;



@Mapper
public interface OrderDetailsMapper {

    OrderDetailsMapper INSTANCE = Mappers.getMapper(OrderDetailsMapper.class);

//    @Mapping(source = "product_id", target = "product")
//    public OrderDetailsCreationDTO mapOrderDetails(OrderDetails orderDetails);
//
//    public OrderDetails mapOrderDetailsCreationDto(OrderDetailsCreationDTO orderDetailDto);
    public List<OrderDetails> mapOrderDetailsCreationDtos(List<OrderDetailsCreationDTO> orderDetailsDto);
}
