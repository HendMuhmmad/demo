package com.example.demo.mapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import com.example.demo.model.dto.CustomerDto;
import com.example.demo.model.orm.Vw_Order_Details;
@Mapper
@Component
public interface CustomerMapper {
	CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);
	
	@Mapping(source="customerName", target="name")
	@Mapping(source="customerAddress", target="address")
	@Mapping(source="customerPhone", target="phone")
	public CustomerDto toDto(Vw_Order_Details orderDetails);
}