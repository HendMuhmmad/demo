package com.example.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import com.example.demo.model.dto.CustomerDto;
import com.example.demo.model.orm.User;
import com.example.demo.model.orm.Vw_Order_Details;

@Mapper
@Component
public interface CustomerMapper {

	CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);
	
	@Mapping(source="customerName", target="name")
	@Mapping(source="customerAddress", target="address")
	@Mapping(source="customerPhone", target="phone")
	public CustomerDto toDto(Vw_Order_Details orderDetails);
	
	@Mapping(source="user",target="name",qualifiedByName = "getCustomerName")
	public CustomerDto toDto(User user);
	
	
	@Named("getCustomerName")
    public static String getCustomerName(User user) {
        return user.getFirstName() + " " + user.getLastName();
    }

}
